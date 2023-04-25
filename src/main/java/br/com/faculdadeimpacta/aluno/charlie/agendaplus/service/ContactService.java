package br.com.faculdadeimpacta.aluno.charlie.agendaplus.service;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.AuditType;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.Contact;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.ContactAudit;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.User;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception.ContactNotFoundException;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.ContactAuditRepository;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.ContactRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.ContactSpecifications.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactAuditRepository contactAuditRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<Contact> listContacts(User user) {
        log.info("requesting contact list for user {}", user);
        return contactRepository.findAll(notDeleted().and(forUser(user)));
    }

    @Transactional
    public Contact createContact(User user, Contact contact) {
        log.info("creating new contact for user {}", user);
        contact.setUser(user);
        var saved = contactRepository.insert(contact);
        contactAuditRepository.save(createContactAudit(saved, user, AuditType.CREATED));
        return saved;
    }

    @Transactional(readOnly = true)
    public Contact findContact(User user, UUID uuid) {
        log.info("finding a contact by uuid {} for user {}", uuid, user);
        return contactRepository.findOne(
                notDeleted()
                        .and(forUser(user))
                        .and(withId(uuid)))
                .orElseThrow(() -> new ContactNotFoundException(uuid));
    }

    @Transactional
    public Contact updateContact(User user, UUID uuid, Contact contact) {
        log.info("updating contact {} for user {}", uuid, user);
        var stored = findContact(user, uuid);
        copyContact(contact, stored);
        var afterSave = contactRepository.save(stored);
        log.info("contact {} saved", user);
        var audit = contactAuditRepository.save(createContactAudit(afterSave, user, AuditType.UPDATED));
        log.info("contact audit {} saved", audit.getUuid());
        return findContact(user, uuid);
    }

    @Transactional
    public void deleteContact(User user, UUID uuid) {
        log.info("deleting contact {} for user {}", uuid, user);
        var contact = findContact(user, uuid);
        contact.setDeletedAt(Instant.now());
        var saved = contactRepository.save(contact);
        contactAuditRepository.save(createContactAudit(saved, user, AuditType.DELETED));
    }

    private static void copyContact(Contact source, Contact target) {
        target.setName(source.getName());
        target.setAvatarUrl(source.getAvatarUrl());
        target.getData().clear();
        target.getData().addAll(source.getData());
        target.getData().forEach(data -> data.setContact(target));
    }

    private ContactAudit createContactAudit(Contact contact, User user, AuditType type) {
        try {
            return ContactAudit.builder()
                    .user(user)
                    .contact(contact)
                    .updatedObject(objectMapper.writeValueAsString(contact))
                    .auditType(type)
                    .occurredAt(Instant.now())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
