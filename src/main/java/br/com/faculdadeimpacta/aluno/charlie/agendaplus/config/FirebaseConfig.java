package br.com.faculdadeimpacta.aluno.charlie.agendaplus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "firebase")
public record FirebaseConfig (String serviceAccountPath) {
    @ConstructorBinding
    public FirebaseConfig {
    }
}
