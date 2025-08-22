package com.ltineraryplanning.tripservice.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
//        Server customServer = new Server();
//        customServer.setUrl("http://localhost:8222"); //
        return new OpenAPI()
                .servers(List.of(new Server().url("http://localhost:8222")))
                .info(new Info()
                        .title("Trip Service API")
                        .version("1.0")
                        .description("This Service is use to add trip and many more trip related task"));

    }
}