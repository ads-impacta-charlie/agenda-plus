package br.com.faculdadeimpacta.aluno.charlie.agendaplus.spring;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.config.FirebaseConfig;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@EnableConfigurationProperties(FirebaseConfig.class)
public class FirebaseSpringConfiguration {
    @Bean
    public FirebaseOptions firebaseOptions(FirebaseConfig config) throws IOException {
        try (var serviceAccountStream = new FileInputStream(config.serviceAccountPath())) {
            return FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();
        }
    }

    @Bean
    public FirebaseApp firebaseApp(FirebaseOptions options) {
        return FirebaseApp.initializeApp(options);
    }
}
