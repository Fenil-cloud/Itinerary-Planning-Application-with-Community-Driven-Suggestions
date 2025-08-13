package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.ServiceImpl;

import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.AddCommentDTO;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.AddSuggestionDTO;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.CommunitySuggestionPoll;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.ResponseDTO;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.entity.Comment;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.entity.Suggestion;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.enums.StatusCodeEnum;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.exception.SuggestionNotFoundException;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.kafka.KafkaProducer;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.kafka.consumer.PollDetailsDTO;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.repo.PollRepo;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.repo.SuggestionRepo;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.service.FeignService;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.service.SuggestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class SuggestionServiceImpl implements SuggestionService {

    @Autowired
    private SuggestionRepo suggestionRepo;

    @Autowired
    private ExtractTokenService tokenService;

    @Autowired
    private AdvCommentImpl Advcomment;

    @Autowired
    private FeignService feignService;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private PollRepo pollRepo;

    @Override
    public ResponseDTO add(String auth, AddSuggestionDTO addSuggestionDTO, String tripID) throws ParseException {

        Map<String, Object> user = tokenService.extractValue(auth);
        Suggestion suggestion = new Suggestion();
        suggestion.setTitle(addSuggestionDTO.getTitle());
        suggestion.setDescription(addSuggestionDTO.getDescription());
        suggestion.setCreatedAt(LocalDateTime.now().toString());
        suggestion.setTripId(tripID);
        suggestion.setUserId(user.get("uid").toString());
        suggestion.setUsername(feignService.getUserName(auth));
        suggestion.setTags(addSuggestionDTO.getTag());
        suggestionRepo.save(suggestion);
        log.info("Suggestion List {}",suggestionRepo.findByUserId(user.get("uid").toString()));
        return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), "Suggestion created successfully", null);
    }

    @Override
    public ResponseDTO postComment(String auth, String suggestionId, AddCommentDTO addCommentDTO) throws ParseException {
        Map<String, Object> user = tokenService.extractValue(auth);

        if (suggestionRepo.existsById(suggestionId)) {
            Comment comment = new Comment();
            var suggestion = suggestionRepo.findById(suggestionId);
            comment.setUserId(suggestion.get().getUsername());
            comment.setText(addCommentDTO.getText());
            comment.setCreatedAt(LocalDateTime.now().toString());
            comment.setCommentId(UUID.randomUUID().toString().replace("-", ""));
            List<Comment> comments = suggestion.get().getComments();
            if (comments == null) {
                comments = new ArrayList<>();
            }
            comments.add(comment);
            suggestion.get().setComments(comments);
            suggestionRepo.save(suggestion.get());
            return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), "Comment has been posted.", null);
        } else {
            throw new SuggestionNotFoundException("Suggestion not found");
        }

    }

    @Override
    public ResponseDTO updateComment(String auth, String suggestionId, String commentId, AddCommentDTO addCommentDTO) throws ParseException {
        Map<String, Object> user = tokenService.extractValue(auth);

        if (suggestionRepo.existsById(suggestionId)) {
            var suggestion = suggestionRepo.findById(suggestionId);
            Comment comment = Advcomment.getCommentById(suggestionId, commentId);
            if (comment != null) {
                if (comment.getUserId().equals(feignService.getUserName(auth))) {
                    Advcomment.updateCommentText(suggestionId, commentId, addCommentDTO.getText());
                    return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), "Comment has been edited.", null);

                } else {
                    return new ResponseDTO(StatusCodeEnum.NOT_FOUND.getStatusCode(), "You can't edit this comment", null);
                }

            } else {
                return new ResponseDTO(StatusCodeEnum.NOT_FOUND.getStatusCode(), "comment not found", null);
            }

        } else {
            throw new SuggestionNotFoundException("Suggestion not found");

        }
    }

    @Override
    public ResponseDTO deleteComment(String auth, String suggestionId, String commentId) {
        if (suggestionRepo.existsById(suggestionId)) {
            var suggestion = suggestionRepo.findById(suggestionId);
            Comment comment = Advcomment.getCommentById(suggestionId, commentId);
            if (comment != null) {
                if (comment.getUserId().equals(feignService.getUserName(auth))) {
                    Advcomment.deleteComment(suggestionId, commentId);
                    return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), "Comment deleted successfully", null);

                }
            } else {
                return new ResponseDTO(StatusCodeEnum.NOT_FOUND.getStatusCode(), "comment not found", null);
            }

        } else {
            throw new SuggestionNotFoundException("Suggestion not found");

        }
        return null;
    }

    @Override
    public ResponseDTO deleteSuggestion(String auth, String suggestionId) {
        if (suggestionRepo.existsById(suggestionId)) {
            var suggestion = suggestionRepo.findById(suggestionId);

            if (suggestion.get().getUsername().equals(feignService.getUserName(auth))) {
                suggestionRepo.deleteById(suggestionId);
                return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), "Suggestion deleted successfully", null);
            } else {
                return new ResponseDTO(StatusCodeEnum.ERROR.getStatusCode(), "You're not authorized to delete this suggestion.", null);
            }


        }
        return new ResponseDTO(StatusCodeEnum.NOT_FOUND.getStatusCode(), "Suggestion not found", null);
    }

    @Override
    public ResponseDTO viewSuggestion(String auth, String suggestionId) {
        var sug = suggestionRepo.findById(suggestionId);
        if(sug.isPresent()){
            return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(),"Suggestions",sug.get());
        }
        else {
            throw new SuggestionNotFoundException("Suggestion not found");
        }
    }

    @Override
    public ResponseDTO viewMyAllSuggestion(String auth) throws ParseException {
        Map<String, Object> user = tokenService.extractValue(auth);
        String UID = user.get("uid").toString();
        List<Suggestion> suggestions =  suggestionRepo.findByUserId(UID);
        if(suggestions != null){
            return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), "All Suggestions",suggestions);
        }
        throw new SuggestionNotFoundException("Suggestion not found");

    }

//    @Override
//    public ResponseDTO createSuggestionPool(String auth, CommunitySuggestionPoll communitySuggestionPoll) throws ParseException {
//        return null;
//    }

    @Override
    public ResponseDTO createSuggestionPoll(String auth, CommunitySuggestionPoll communitySuggestionPoll, String suggestionId) throws ParseException {
        Map<String, Object> user = tokenService.extractValue(auth);
        var suggestion = suggestionRepo.findById(suggestionId);
        if(suggestion == null){
            throw new SuggestionNotFoundException("Suggestion not found");
        }
        if(user.get("uid").toString().equalsIgnoreCase(suggestion.get().getUserId())){
            communitySuggestionPoll.setTripId(suggestion.get().getTripId());
            communitySuggestionPoll.setType("suggestion");
            communitySuggestionPoll.setSuggestionID(suggestionId);
            communitySuggestionPoll.setUId(suggestion.get().getUserId());
            communitySuggestionPoll.setUserName(suggestion.get().getUsername());
            kafkaProducer.sendSuggestionPoll(communitySuggestionPoll);
            return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(),"Pool created successfully",null);
        }
        else {
            return new ResponseDTO(StatusCodeEnum.ERROR.getStatusCode(),"You can't create pool for others suggestion",null);
        }

    }

    @Override
    public ResponseDTO getAllPolls(String auth, String suggestionId) {
        List<PollDetailsDTO> pollDetailsDTO = pollRepo.findBySuggestionId(suggestionId);
        if(pollDetailsDTO.isEmpty()){
            return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(),"polls not found on given suggestion",pollDetailsDTO);

        }
        return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(),"polls",pollDetailsDTO);

    }
}

