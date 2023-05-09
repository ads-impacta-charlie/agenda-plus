package br.com.faculdadeimpacta.aluno.charlie.agendaplus.handler.http;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.WithFirebaseAuth;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.User;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.UserPreferences;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception.PreferenceNotFoundException;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.UserPreferenceAuditRepository;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.UserPreferenceRepository;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class UserPreferencesHandlerTest {

    @Autowired
    EntityManager entityManager;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserPreferenceRepository preferenceRepository;
    @Autowired
    UserPreferenceAuditRepository auditRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;
    User user;

    private static RequestPostProcessor firebaseUser() {
        return jwt().jwt(builder -> builder.subject("test-user-id"));
    }

    private ResultActions list() throws Exception {
        return mockMvc.perform(get("/preferences"));
    }

    private List<UserPreferences> update(List<UserPreferences> preferences) throws Exception {
        var responseJson = mockMvc.perform(put("/preferences")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(preferences))
                        .with(firebaseUser()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(responseJson, new TypeReference<>() {
        });
    }

    private UserPreferences createPreference(String key, String value) {
        return UserPreferences.builder()
                .user(user)
                .key(key)
                .value(value)
                .build();
    }

    @Nested
    @WithAnonymousUser
    public class Anonymous {
        @Test
        public void shouldNotAllowAnonymousRequests() throws Exception {
            list().andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @WithFirebaseAuth
    public class Authenticated {
        @BeforeEach
        public void setupUser() {
            user = userRepository.save(User.builder().firebaseUserId("test-user-id").build());
        }

        @AfterEach
        public void cleanupUser() {
            auditRepository.deleteAll();
            preferenceRepository.deleteAll();
            userRepository.deleteAll();
        }

        @Test
        public void shouldReturnEmptyList() throws Exception {
            list()
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));
        }

        @Test
        public void shouldCreatePreferences() throws Exception {
            var preferences = List.of(
                    createPreference("key-a", "value-a"),
                    createPreference("key-b", "value-b"),
                    createPreference("key-c", "value-c"),
                    createPreference("key-d", "value-d"));

            mockMvc.perform(post("/preferences")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(preferences))
                            .with(firebaseUser()))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.length()").value(4),
                            jsonPath("$[0].uuid").isNotEmpty());
        }

        @Test
        public void listShouldContainCreatedPreference() throws Exception {
            var preference = preferenceRepository.save(createPreference("key-a", "value-a"));
            list().andExpectAll(
                    status().isOk(),
                    jsonPath("$[0].uuid").value(preference.getUuid().toString()));
        }

        @Test
        public void shouldUpdate() throws Exception {
            var preference = preferenceRepository.save(createPreference("key-a", "value-a"));
            preference.setValue("updated-value");

            var response = update(List.of(preference));

            assertThat(response)
                    .isNotNull()
                    .hasSize(1)
                    .element(0)
                    .extracting(UserPreferences::getValue)
                    .isEqualTo("updated-value");
        }

        @Test
        public void shouldNotUpdateWhenConfigurationNotFound() throws Exception {
            var preference = preferenceRepository.save(createPreference("key-a", "value-a"));
            preference.setValue("updated-value");
            preference.setKey("invalid-key");

            var responseJson = mockMvc.perform(put("/preferences")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(List.of(preference)))
                            .with(firebaseUser()))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            var response = objectMapper.readValue(responseJson, PreferenceNotFoundException.class);

            assertThat(response)
                    .isNotNull()
                    .extracting(PreferenceNotFoundException::getKey)
                    .isEqualTo("invalid-key");
        }

        @Test
        public void shouldDelete() throws Exception {
            var preference = preferenceRepository.save(createPreference("key-a", "value-a"));

            mockMvc.perform(delete("/preferences")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsBytes(List.of(preference.getUuid())))
                            .with(firebaseUser()))
                    .andExpect(status().isNoContent());

            var storedPreference = preferenceRepository.findById(preference.getUuid());
            assertThat(storedPreference)
                    .isPresent()
                    .get()
                    .extracting(UserPreferences::getDeletedAt)
                    .isNotNull();
        }

        @Test
        public void shouldNotDeleteWhenNotFound() throws Exception {
            var preference = preferenceRepository.save(createPreference("key-a", "value-a"));

            mockMvc.perform(delete("/preferences")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsBytes(List.of(preference.getUuid(), UUID.randomUUID())))
                            .with(firebaseUser()))
                    .andExpect(status().isNotFound());

            var storedPreference = preferenceRepository.findById(preference.getUuid());
            assertThat(storedPreference)
                    .isPresent()
                    .get()
                    .extracting(UserPreferences::getDeletedAt)
                    .isNull();
        }
    }
}
