package br.com.faculdadeimpacta.aluno.charlie.agendaplus.handler.http;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.Contact;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.ContactData;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.ContactDataCategory;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.ContactDataType;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.ContactRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContactHandlerIntegrationTest {
    private static final UUID INVALID_CONTACT_UUID = UUID.randomUUID();
    private static final String CONTACT_NAME = "John Doe";

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private ContactRepository repository;
    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    public void setupUrl() {
        baseUrl = "http://localhost:" + port + "/contact";
    }

    @AfterEach
    public void cleanup() {
        repository.deleteAll();
    }

    @Test
    public void shouldReturnEmptyContactList() {
        var response = getContactList();

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    public void shouldReturnNotFoundWhenContactDoesNotExist() {
        var response = this.restTemplate.getForEntity(
                baseUrl + INVALID_CONTACT_UUID,
                Contact.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldCreateContact() {
        var contact = createContact();

        var response = this.restTemplate.postForObject(baseUrl, contact, Contact.class);

        assertThat(response.getUuid()).isNotNull();
    }

    @Test
    public void listShouldContainCreatedContact() {
        repository.save(createContact());
        var response = getContactList();

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        var contactList = response.getBody();
        assertThat(contactList)
                .isNotNull()
                .hasSize(1);
        assertThat(contactList.get(0).getUuid())
                .isNotNull();
    }

    @Test
    public void shouldReturnContactData() {
        var expected = createContact();
        repository.save(expected);

        var response = this.restTemplate.getForEntity(baseUrl + "/" + expected.getUuid(), Contact.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isEqualTo(expected);
    }

    @Test
    public void shouldUpdateContact() {
        var expected = createContact();
        repository.save(expected);
        expected.setName("Test name");

        var response = this.restTemplate.exchange(
                baseUrl + "/" + expected.getUuid(),
                HttpMethod.PUT,
                new HttpEntity<>(expected),
                Contact.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .extracting(Contact::getName)
                .isEqualTo("Test name");
    }

    @Test
    public void shouldDeleteContact() {
        var expected = createContact();
        repository.save(expected);

        var response = this.restTemplate.exchange(
                baseUrl + "/" + expected.getUuid(),
                HttpMethod.DELETE,
                new HttpEntity<String>(null, null),
                Void.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    private ResponseEntity<List<Contact>> getContactList() {
        var request = RequestEntity.get(baseUrl).build();
        return this.restTemplate.exchange(request, new ParameterizedTypeReference<>() {
        });
    }

    private Contact createContact() {
        var contactData = ContactData.builder()
                .category(ContactDataCategory.BUSINESS)
                .type(ContactDataType.TELEPHONE)
                .value("+1 234 5678901")
                .build();
        return Contact.builder()
                .name(CONTACT_NAME)
                .data(List.of(contactData))
                .build();
    }
}
