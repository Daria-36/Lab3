package com.tokyo.magic.archive.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI missionArchiveOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tokyo Magic College Mission Archive API")
                        .version("1.0.0")
                        .description("HTTP API для загрузки миссий, просмотра архива и генерации отчетов."));
    }
}
