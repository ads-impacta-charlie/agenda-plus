package br.com.faculdadeimpacta.aluno.charlie.agendaplus.service;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.*;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception.ContactNotFoundException;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.ContactAuditRepository;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.ContactRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {
    @Mock
    private ContactRepository contactRepository;
    @Mock
    private ContactAuditRepository contactAuditRepository;
    @Captor
    private ArgumentCaptor<ContactAudit> contactAuditArgumentCaptor;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    private ContactService contactService;

    private User user;
    private Contact contact;

    @BeforeEach
    public void setupUser() {
        user = User.builder()
                .uuid(UUID.randomUUID())
                .build();
        var data = ContactData.builder()
                .uuid(UUID.randomUUID())
                .category(ContactDataCategory.BUSINESS)
                .type(ContactDataType.EMAIL)
                .value("foo@bar.com")
                .build();
        var dataList = new ArrayList<ContactData>();
        dataList.add(data);
        contact = Contact.builder()
                .uuid(UUID.randomUUID())
                .data(dataList)
                .build();
    }

    @Test
    public void testListContacts() {
        doReturn(Collections.emptyList())
                .when(contactRepository)
                .findAll(any());

        var contacts = contactService.listContacts(user);

        assertThat(contacts).isEmpty();
        verify(contactRepository).findAll(any());
    }

    @Test
    public void testCreateContact() throws JsonProcessingException {
        doReturn(contact)
                .when(contactRepository)
                .insert(eq(contact));

        contactService.createContact(user, contact);

        verify(contactRepository).insert(eq(contact));
        verify(contactAuditRepository).save(contactAuditArgumentCaptor.capture());

        var audit = contactAuditArgumentCaptor.getValue();
        assertThat(audit).isNotNull();
        assertThat(audit.getContact()).isEqualTo(contact);
        assertThat(audit.getUpdatedObject()).isEqualTo(objectMapper.writeValueAsString(contact));
        assertThat(audit.getAuditType()).isEqualTo(AuditType.CREATED);
    }

    @Test
    public void testFindContactNotFound() {
        doReturn(Optional.empty())
                .when(contactRepository)
                .findOne(any());

        try {
            contactService.findContact(user, contact.getUuid());
            Assertions.fail("expected ContactNotFoundException");
        } catch (ContactNotFoundException ignore) { }
    }

    @Test
    public void testFindContactFound() {
        doReturn(Optional.of(contact))
                .when(contactRepository)
                .findOne(any());

        var got = contactService.findContact(user, contact.getUuid());

        assertThat(got).isEqualTo(contact);
    }

    @Test
    public void testUpdateContact() throws JsonProcessingException {
        doReturn(contact)
                .when(contactRepository)
                .save(eq(contact));
        doReturn(Optional.of(contact))
                .when(contactRepository)
                .findOne(any());
        doReturn(new ContactAudit())
                .when(contactAuditRepository)
                .save(any());

        contactService.updateContact(user, contact.getUuid(), contact);

        verify(contactRepository).save(eq(contact));
        verify(contactAuditRepository).save(contactAuditArgumentCaptor.capture());

        var audit = contactAuditArgumentCaptor.getValue();
        assertThat(audit).isNotNull();
        assertThat(audit.getContact()).isEqualTo(contact);
        assertThat(audit.getUpdatedObject()).isEqualTo(objectMapper.writeValueAsString(contact));
        assertThat(audit.getAuditType()).isEqualTo(AuditType.UPDATED);
    }

    @Test
    public void testDeleteContact() throws JsonProcessingException {
        doReturn(contact)
                .when(contactRepository)
                .save(eq(contact));
        doReturn(Optional.of(contact))
                .when(contactRepository)
                .findOne(any());
        doReturn(new ContactAudit())
                .when(contactAuditRepository)
                .save(any());

        contactService.deleteContact(user, contact.getUuid());

        var contactCaptor = ArgumentCaptor.forClass(Contact.class);
        verify(contactRepository).save(contactCaptor.capture());
        assertThat(contactCaptor.getValue().getDeletedAt()).isNotNull();

        verify(contactAuditRepository).save(contactAuditArgumentCaptor.capture());

        var audit = contactAuditArgumentCaptor.getValue();
        assertThat(audit).isNotNull();
        assertThat(audit.getContact()).isEqualTo(contact);
        assertThat(audit.getUpdatedObject()).isEqualTo(objectMapper.writeValueAsString(contact));
        assertThat(audit.getAuditType()).isEqualTo(AuditType.DELETED);
    }
}