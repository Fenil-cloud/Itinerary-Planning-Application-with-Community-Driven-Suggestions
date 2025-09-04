package com.ltineraryplanning.authservice.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltineraryplanning.authservice.Exception.UserNotFoundException;
import com.ltineraryplanning.authservice.dto.*;
import com.ltineraryplanning.authservice.kafka.AuthDto;
import com.ltineraryplanning.authservice.kafka.AuthLinkProducer;
import com.ltineraryplanning.authservice.kafka.RedisTokenDto;
import com.ltineraryplanning.authservice.model.User;
import com.ltineraryplanning.authservice.model.UserOtp;
import com.ltineraryplanning.authservice.model.UserVerification;
import com.ltineraryplanning.authservice.repo.OtpRepo;
import com.ltineraryplanning.authservice.repo.UserRepo;
import com.ltineraryplanning.authservice.repo.UserVerificationRepo;
import com.ltineraryplanning.authservice.service.AuthService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;
    @Value("${keycloak.admin-username}")
    private String adminUsername;
    @Value("${keycloak.admin-password}")
    private String adminPassword;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OtpRepo otpRepo;

    @Autowired
    private AuthLinkProducer authLinkProducer;

    @Autowired
    private TwilioService smsService;

    @Autowired
    private ExtractTokenService tokenService;

    @Override
    public ResponseDTO registerUser(RegisterRequest request) {
        String adminToken  = getAdminToken();
//        log.info("admin token : {}",adminToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create user
        Map<String,Object> user = new HashMap<>();
        user.put("username",request.getUsername().toLowerCase());
        user.put("enabled",true);
        user.put("email",request.getEmail());
        user.put("firstName", request.getFirstName());  // <-- Add this
        user.put("lastName", request.getLastName());
//        user.put("contact",request.getContact());

        HttpEntity<Map<String,Object>> createUserRequest= new HttpEntity<>(user,headers);
        ResponseEntity<?> createResponse = restTemplate.postForEntity(
                keycloakServerUrl + "/admin/realms/" + realm + "/users",
                createUserRequest,
                Void.class
        );

        if (!createResponse.getStatusCode().is2xxSuccessful()){
            return new ResponseDTO("401","User registration fail", false);
        }
        // Fetch created user ID
        ResponseEntity<UserRepresentation[]> response = restTemplate.exchange(
                keycloakServerUrl + "/admin/realms/" + realm + "/users?username=" + request.getUsername(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserRepresentation[].class
        );
        String userId = Objects.requireNonNull(response.getBody())[0].getId();
        System.err.println(userId);
        try{
            if(request.getRoles().toLowerCase().equals("organizer")) {
                setRole(request.getRoles().toLowerCase(), adminToken, userId);
            }
            AuthDto authDto = new AuthDto();
            String URL = generateUrl(userId,request.getEmail());
            authDto.setUrl(URL);
            authDto.setEmail(request.getEmail());
            authDto.setFname(request.getFirstName());
            authLinkProducer.sendAuthLink(authDto);
            System.err.println(URL);



            User user1 = new User();
            user1.setEmail(request.getEmail());
            user1.setActive(true);
            user1.setGender(request.getGender());
            user1.setUid(userId);
            user1.setVerifiedMobile(false);
            user1.setRoles(request.getRoles());
            user1.setPhoneNumber(request.getPhoneNumber());
            user1.setUsername(request.getUsername());
            user1.setFirstName(request.getFirstName());
            user1.setLastName(request.getLastName());

            List<UserOtp> listOTP = new ArrayList<>();
            UserOtp otp = new UserOtp();
            String otp_number = generateOTP();
//            OTP
//            smsService.sendOTP(request.getPhoneNumber(), "OTP "+otp_number+" valid for 5 minutes.");
            System.err.println(otp_number);
//            listOTP.add(otp);
            otp.setOTP(otp_number);
            otp.setExpiryDate(LocalDateTime.now().plusMinutes(5));
            otp.setSentDate(LocalDateTime.now());
            user1.setOtp(listOTP);
            var data = userRepo.save(user1);
            otp.setUser(data);
            otpRepo.save(otp);



        }catch (MessagingException exception){
//            log.info("Exception :: {}",exception.getMessage());
        }

        Map<String, Object> passwordPayload = Map.of(
                "type", "password",
                "value", request.getPassword(),
                "temporary", false
        );
        HttpEntity<Object> passwordRequest = new HttpEntity<>(passwordPayload, headers);

        restTemplate.put(
                keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password",
                passwordRequest
        );

        return new ResponseDTO("201","User registration Success",true);

    }


    private String getAdminToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=admin-cli" +
                "&username=" + adminUsername +
                "&password=" + adminPassword +
                "&grant_type=password";

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                keycloakServerUrl + "/realms/master/protocol/openid-connect/token",
                HttpMethod.POST,
                request,
                TokenResponse.class
        );

        return response.getBody().getAccess_token();
    }

    @Autowired
    private UserVerificationRepo repo;
    public String generateUrl(String id,String email){
        String token = UUID.randomUUID().toString();
        UserVerification userVerification = new UserVerification();
        userVerification.setUserId(id);
        userVerification.setVerified(false);
        userVerification.setExpiryDate(LocalDateTime.now().plusHours(24));
        userVerification.setToken(token);
        userVerification.setType("verify");
        userVerification.setEmail(email);
        userVerification.setSendAt(LocalDateTime.now());
        repo.save(userVerification);
        return "http://localhost:8222/api/v1/auth/verify/" + id +"/"+ token;

    }




    @Override
    public ResponseDTO login(LoginRequest request) {
            User user = userRepo.findByUsername(request.getUsername());
            if(!user.isVerifiedEmail()){
                return new ResponseDTO("200","Email not verified!",null);
            }
            if(!user.isActive()){
                return new ResponseDTO("200","User account not active!",null);
            }
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//                log.info("client_id + {}",clientId);
//                log.info("client_secret + {}",clientSecret);
//                log.info("password {} ",request.getPassword());
//                log.info("username {} ",request.getUsername());



                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                body.add("client_id", "ltinerary-planning");
                body.add("client_secret", clientSecret);
                body.add("username", request.getUsername().toLowerCase());
                body.add("password", request.getPassword());
                body.add("grant_type", "password");



                HttpEntity<MultiValueMap<String, String>> loginRequest = new HttpEntity<>(body, headers);

                ResponseEntity<Map> response = restTemplate.exchange(
                        keycloakServerUrl + "/realms/Itinerary-planning/protocol/openid-connect/token",
                        HttpMethod.POST,
                        loginRequest,
                        Map.class
                );

                Map<String,String> obj = new HashMap<>();
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    String accessToken = (String) response.getBody().get("access_token");
                    String RefreshToken = (String) response.getBody().get("refresh_token");
//                todo-produce refresh token
                    SignedJWT signedJWT = SignedJWT.parse(accessToken);
                    JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
                    String id = claims.getStringClaim("sub");
                    log.info("SUB ID :: {}",id);

                    RedisTokenDto data = new RedisTokenDto();
                    data.setId(id);
                    data.setRefresh_token(RefreshToken);
                    data.setAccess_token(accessToken);
                    authLinkProducer.sendRefreshToken(data);

                    obj.put("token",accessToken);
//    todo            obj.put("refresh_token",RefreshToken);

                    return new ResponseDTO("200", "Login Success", obj);
                }
                obj.put("token","");
//    todo        obj.put("refresh_token","");
                return new ResponseDTO("fail", "Login failed", obj);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Invalid login credentials");

                return new ResponseDTO("400","Invalid login credentials",null);

            }
        }

    @Override
    public ResponseDTO verify(String id, String token) {
        Optional<UserVerification> user = repo.findById(id);
        if(!user.isPresent()){
            return new ResponseDTO("404","invalid url",null);
        }
        if(user.get().isVerified()){
            return new ResponseDTO("200","User is already verified.",null);
        }
        String UID = user.get().getUserId();
        User u1 = userRepo.findByUid(UID);
        if(user.get().getExpiryDate().isBefore(LocalDateTime.now())){
            if(ResendVerificationToken(id,user.get().getEmail())) {
                return new ResponseDTO("200", "link expired..", null);
            }
        }
        String authToken = loginAdmin();
//        log.warn(authToken);

        RestTemplate restTemplate = new RestTemplate();
        String Url = keycloakServerUrl+"/admin/realms/"+realm+"/users/"+id;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("emailVerified", true);
        if(user.get().getToken().equalsIgnoreCase(token)){
            if(user.get().getExpiryDate().isAfter(LocalDateTime.now())){
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
                ResponseEntity<Void> response = restTemplate.exchange(Url, HttpMethod.PUT, request, Void.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    user.get().setVerified(true);
                    repo.save(user.get());
                    u1.setVerifiedEmail(true);
                    userRepo.save(u1);
                    return new ResponseDTO(response.getStatusCode().toString(),"email verified",null);
                } else {
                    return new ResponseDTO(response.getStatusCode().toString(),"email verification failed",null);
                }
            }
        }
        return new ResponseDTO("","",null);
    }

    public boolean ResendVerificationToken(String id,String email){
        String token = UUID.randomUUID().toString();
        var userVerification = repo.findById(id);
        userVerification.get().setToken(token);
        userVerification.get().setSendAt(LocalDateTime.now());
        userVerification.get().setExpiryDate(LocalDateTime.now().plusHours(24));
        repo.save(userVerification.get());

        try{
            AuthDto authDto = new AuthDto();
            String URL = "http://localhost:8222/api/v1/auth/verify/" + id +"/"+ userVerification.get().getToken();
            authDto.setUrl(URL);
            authDto.setEmail(email);
            authDto.setFname("");
            authLinkProducer.sendAuthLink(authDto);
        }catch (MessagingException exception){
//            log.info("Resend Exception :: {}",exception.getMessage());
        }
        return true;


    }

    public String loginAdmin() {

        try {
            RestTemplate restTemplate = new RestTemplate();
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            // Set form parameters
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("username",adminUsername);
            form.add("password",adminPassword);
            form.add("grant_type", "password");
            form.add("client_id", "admin-cli");
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

            // Send POST request
            String url = keycloakServerUrl+"/realms/master/protocol/openid-connect/token";
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    request,
                    String.class
            );
//            System.out.println("Response: " + response.getBody());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("access_token").asText();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;

        }
    }
    public String generateOTP(){
        int randomSixDigit = ThreadLocalRandom.current().nextInt(100000, 1000000);
         return String.valueOf(randomSixDigit);

    }

    public ResponseDTO verifyMobile(String auth,String otp_num) throws ParseException {
        Map<String,Object> user_data = tokenService.extractValue(auth);
        User users = userRepo.findByEmail(user_data.get("email").toString());
        if (users != null) {
            List<UserOtp> otp = users.getOtp();
            UserOtp LatestValue = otp.stream()
                    .max(Comparator.comparing(UserOtp::getSentDate))
                    .orElse(null);
            System.out.println(LatestValue.getOTP());
            if (users.isVerifiedMobile()) {
                return new ResponseDTO("400", "MOBILE NUMBER ALREADY VERIFIED", null);
            }
            if (!otp_num.equals(LatestValue.getOTP())) {
                return new ResponseDTO("400", "Invalid OTP", null);
            }
            if(LatestValue.getExpiryDate().isBefore(LocalDateTime.now())) {
                int randomSixDigit = ThreadLocalRandom.current().nextInt(100000, 1000000);
                String strOTP = String.valueOf(randomSixDigit);

//                List<OTP> listOTP = new ArrayList<>();
                smsService.sendOTP(users.getPhoneNumber(), "OTP "+strOTP+" Valid for 5 minutes.");

                UserOtp otpobj = new UserOtp();
                otpobj.setUser(users);
                otpobj.setOTP(strOTP);
                otpobj.setExpiryDate(LocalDateTime.now().plusMinutes(5));
                otpobj.setSentDate(LocalDateTime.now());
                otpRepo.save(otpobj);

                return new ResponseDTO("400", "YOUR OTP IS EXPIRED --- NEW OTP SENT.", null);
            }
            LatestValue.setVerificationTime(LocalDateTime.now());
            users.setOtp(otp);
            users.setVerifiedMobile(true);
            userRepo.save(users);
            return new ResponseDTO("200", "MOBILE VERIFICATION SUCCESSFUL", null);

        } else {
            throw new UserNotFoundException("USER NOT FOUND");
        }
    }



    @Override
    public ResponseDTO resetPassword(String uid, String token, NewPasswordDTO newPasswordDTO) {
        Optional<UserVerification> userVerification = repo.findById(uid);
        if(userVerification.isEmpty()){
            return new ResponseDTO("200","invalid url",null);
        }
        if(!userVerification.get().getToken().equals(token) || userVerification.get().getIsCheck()){
            return new ResponseDTO("200","invalid url",null);
        }
        if(!userVerification.get().getExpiryDate().isAfter(LocalDateTime.now())){
            return new ResponseDTO("200","Link expired",null);
        }
        if(!newPasswordDTO.getPassword().equals(newPasswordDTO.getConfirmPassword())){
            return new ResponseDTO("200","Password and Confirm password does not match!",200);

        }
        if(userVerification.get().getType().equals("reset")){
            String url = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + uid + "/reset-password";
            Map<String, Object> body = new HashMap<>();
            body.put("type", "password");
            body.put("temporary", false);  // set to true if you want user to change it on next login
            body.put("value", newPasswordDTO.getPassword());

            String adminToken  = getAdminToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(adminToken);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    Void.class
            );
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                userVerification.get().setIsCheck(true);
                repo.save(userVerification.get());
                return new ResponseDTO(response.getStatusCode().toString(),"Password reset success",null);
            } else {
                return new ResponseDTO(response.getStatusCode().toString(),"Password reset failed",null);
            }
        }


        return null;
    }

    @Override
    public List<EmailAndFirstNameDTO> getEmailAndFirstName(List<String> usernames) {
        List<User> users = userRepo.findAllById(usernames);
        List<EmailAndFirstNameDTO> list = users.stream().map(m-> {
                    EmailAndFirstNameDTO dto = new EmailAndFirstNameDTO();
                    dto.setFirstName(m.getFirstName());
                    dto.setEmail(m.getEmail());
                    return dto;
                }
        ).toList();
        return list;
    }

    public String getRoleId(String role,String authToken) {
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/roles/" + role;
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> roles = response.getBody();
                if (roles != null) {
                    System.out.println("Role ID: " + roles.get("id"));
                    return roles.get("id").toString();
                }
            } else {
                System.out.println("Failed: " + response.getStatusCode());
                return "";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return "";
    }


    public void setRole(String roleName, String authToken,String userId) {

        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
        Map<String, String> role = new HashMap<>();
        String roleId = getRoleId(roleName,authToken);
        if(roleId != "") {
            role.put("id", roleId); // role UUID
            role.put("name", roleName);
            List<Map<String, String>> requestBody = Collections.singletonList(role);
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<List<Map<String, String>>> entity = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            System.out.println("Response: " + response.getStatusCode());

        }
    }

}

