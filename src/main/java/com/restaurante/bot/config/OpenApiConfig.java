package com.restaurante.bot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Movete Switchs - API")
                        .version("v1")
                        .description("Documentación OpenAPI/Swagger de los endpoints del servicio")
                        .contact(new Contact().name("Arquitecsoft").email("devops@arquitecsoft.com"))
                        .license(new License().name("Proprietary").url("https://example.com"))
                )
                .addServersItem(new Server().url("http://localhost:8080").description("Local server"))
                .externalDocs(new ExternalDocumentation().description("Proyecto repo").url("https://example.com"));
    }
}
