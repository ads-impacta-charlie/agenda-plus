package br.com.faculdadeimpacta.aluno.charlie.agendaplus.handler.http;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.WithFirebaseAuth;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.*;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.ContactAuditRepository;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.ContactRepository;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.UserRepository;
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

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ContactHandlerTest {
    private static final UUID INVALID_CONTACT_UUID = UUID.randomUUID();
    private static final String CONTACT_NAME = "John Doe";

    @Autowired
    EntityManager entityManager;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ContactRepository contactRepository;
    @Autowired
    ContactAuditRepository contactAuditRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;
    User user;

    @Nested
    @WithAnonymousUser
    public class Anonymous {
        @Test
        public void shouldNotAllowAnonymousRequests() throws Exception {
            getContactList()
                    .andExpect(status().isUnauthorized());
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
            System.out.println("deleting stuff");
            contactAuditRepository.deleteAll();
            contactRepository.deleteAll();
            userRepository.deleteAll();
        }

        @Test
        public void shouldReturnEmptyContactList() throws Exception {
            getContactList()
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));
        }

        @Test
        public void shouldReturnNotFoundWhenContactDoesNotExist() throws Exception {
            mockMvc.perform(get("/contact/"+INVALID_CONTACT_UUID)
                            .with(firebaseUser()))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void shouldCreateContact() throws Exception {
            var contact = createContact();

            mockMvc.perform(post("/contact")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(contact))
                            .with(firebaseUser()))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.uuid").isNotEmpty());
        }

        @Test
        public void listShouldContainCreatedContact() throws Exception {
            var contact = contactRepository.save(createContact());
            getContactList()
                    .andExpectAll(
                            status().isOk(),
                            content().json("[{\"uuid\":\"" + contact.getUuid() + "\"}]"));
        }

        @Test
        public void shouldReturnContactData() throws Exception {
            var expected = createContact();
            contactRepository.save(expected);

            var responseJson = mockMvc.perform(get("/contact/" + expected.getUuid())
                            .accept(MediaType.APPLICATION_JSON)
                            .with(firebaseUser()))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            var responseObj = objectMapper.readValue(responseJson, Contact.class);
            assertThat(responseObj).isEqualTo(expected);
        }

        @Test
        public void shouldUpdateContact() throws Exception {
            var expected = createContact();
            contactRepository.save(expected);
            expected.setName("Test name");

            var response = updateContact(expected);

            assertThat(response)
                    .isNotNull()
                    .extracting(Contact::getName)
                    .isEqualTo("Test name");
        }


        @Test
        public void shouldDeleteContact() throws Exception {
            var expected = createContact();
            contactRepository.save(expected);

            mockMvc.perform(delete("/contact/" + expected.getUuid())
                            .accept(MediaType.APPLICATION_JSON)
                            .with(firebaseUser()))
                    .andExpect(status().isNoContent());

            var contact = contactRepository.findById(expected.getUuid());
            assertThat(contact)
                    .isPresent()
                    .get()
                    .extracting(Contact::getDeletedAt)
                    .isNotNull();
        }

        @Test
        public void shouldDeleteContactData() throws Exception {
            var expected = createContact();
            expected.getData().add(ContactData.builder()
                    .category(ContactDataCategory.PERSONAL)
                    .type(ContactDataType.EMAIL)
                    .value("do.not@delete.this")
                    .contact(expected)
                    .build());
            contactRepository.save(expected);
            expected.getData().remove(0);
            expected.getData().get(0).setCategory(ContactDataCategory.BUSINESS);
            expected.getData().add(ContactData.builder()
                    .category(ContactDataCategory.BUSINESS)
                    .type(ContactDataType.TELEPHONE)
                    .value("+123456")
                    .build());


            var response = updateContact(expected);

            assertThat(response).isNotNull();
            assertThat(response.getData()).hasSize(2);
            assertThat(response.getData().get(0))
                    .satisfies(data -> {
                        assertThat(data.getType()).isEqualTo(ContactDataType.EMAIL);
                        assertThat(data.getCategory()).isEqualTo(ContactDataCategory.BUSINESS);
                        assertThat(data.getValue()).isEqualTo("do.not@delete.this");
                    });
            assertThat(response.getData().get(1))
                    .satisfies(data -> {
                        assertThat(data.getType()).isEqualTo(ContactDataType.TELEPHONE);
                        assertThat(data.getCategory()).isEqualTo(ContactDataCategory.BUSINESS);
                        assertThat(data.getValue()).isEqualTo("+123456");
                    });

            var saved = contactRepository.findById(expected.getUuid());

            assertThat(saved).isPresent();
            assertThat(saved.get().getData())
                    .hasSize(2);
        }
    }

    private ResultActions getContactList() throws Exception {
        return mockMvc.perform(get("/contact"));
    }

    private Contact updateContact(Contact contact) throws Exception {
        var responseJson = mockMvc.perform(put("/contact/" + contact.getUuid())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(contact))
                        .with(firebaseUser()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(responseJson, Contact.class);
    }

    private Contact createContact() {
        var contactData = ContactData.builder()
                .category(ContactDataCategory.BUSINESS)
                .type(ContactDataType.TELEPHONE)
                .value("+1 234 5678901")
                .build();
        var data = new ArrayList<ContactData>();
        data.add(contactData);
        var contact = Contact.builder()
                .name(CONTACT_NAME)
                .data(data)
                .user(user)
                .build();
        contactData.setContact(contact);
        return contact;
    }

    private static RequestPostProcessor firebaseUser() {
        return jwt().jwt(builder -> builder.subject("test-user-id"));
    }
}
