package com.alpharedge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AlphaEdge API")
                        .description("Crypto Intelligence Platform - Real-time cryptocurrency tracking, portfolio management, and technical analysis")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("AlphaEdge Team")
                                .url("https://alpharedge.com")
                                .email("support@alpharedge.com")));
    }
}
