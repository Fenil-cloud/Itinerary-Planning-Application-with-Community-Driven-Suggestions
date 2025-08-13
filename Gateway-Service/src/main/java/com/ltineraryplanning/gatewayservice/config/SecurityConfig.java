package com.ltineraryplanning.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity){
        serverHttpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange->
                                exchange.pathMatchers("/eureka/**","/api/v1/auth/login","/api/v1/auth/register/**","/api/v1/auth/verify/**","verify/mobile","/api/v1/auth/getEmailAndFirstName")
                                        .permitAll()
//                                        .pathMatchers("").hasRole("service")
//                                        .pathMatchers("").hasRole("employee")
//                                        .pathMatchers("").hasRole("admin")
                                        .anyExchange()
                                        .authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwtSpec ->
                                jwtSpec.jwtAuthenticationConverter(new KeycloakRoleConverter())
                        )
                );
        return serverHttpSecurity.build();
    }

}
