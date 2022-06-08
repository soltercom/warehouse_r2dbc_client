package ru.spb.altercom.warehouse_r2dbc_client.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {

    @Bean
    public WebClient webClient() {
        return WebClient
            .builder()
            .baseUrl("http://localhost:8090/api")
            .build();
    }

}
