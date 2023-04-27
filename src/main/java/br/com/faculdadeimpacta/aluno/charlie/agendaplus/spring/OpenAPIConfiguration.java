package br.com.faculdadeimpacta.aluno.charlie.agendaplus.spring;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AgendaPlus API",
                description = "This is the API to communicate with AgendaPlus backend service",
                version = "v1"))
@SecurityScheme(
        name = "security_auth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class OpenAPIConfiguration {
}
