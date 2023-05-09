package br.com.faculdadeimpacta.aluno.charlie.agendaplus.service;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.*;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception.ContactNotFoundException;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.ContactAuditRepository;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.ContactRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.ContactSpecifications.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactAuditRepository contactAuditRepository;
    private final ObjectMapper objectMapper;
    private final PhoneValidationService phoneValidationService;

    @Transactional(readOnly = true)
    public List<Contact> listContacts(User user) {
        log.info("requesting contact list for user {}", user);
        return contactRepository.findAll(notDeleted().and(forUser(user)));
    }

    @Transactional
    public Contact createContact(User user, Contact contact) {
        log.info("creating new contact for user {}", user);
        contact.setUser(user);
        validatePhoneNumber(user, contact);
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
        validatePhoneNumber(user, contact);
        copyContact(contact, stored);
        return updateContact(user, stored);
    }

    @Transactional
    public void deleteContact(User user, UUID uuid) {
        log.info("deleting contact {} for user {}", uuid, user);
        var contact = findContact(user, uuid);
        deleteContact(user, contact);
    }


    @Transactional
    public Map<UUID, Set<UUID>> findDuplicates(User user) {
        log.info("finding duplicates for user {}", user);
        var duplicatesByName = contactRepository.findDuplicatesByName(user);
        log.debug("finding duplicates by contact data");
        var duplicatesByContactData = contactRepository.findDuplicatesByContactData(user);
        log.debug("joining duplicates");
        var duplicates = mapDuplicatesByUuid(Stream.concat(duplicatesByContactData, duplicatesByName));
        log.debug("found {} duplicates", duplicates.size());

        var duplicatesUUIDs = new HashSet<>(duplicates.keySet());
        duplicates.forEach((key, value) -> {
            if (duplicatesUUIDs.contains(key)) {
                value.forEach(duplicatesUUIDs::remove);
            }
        });
        duplicates.entrySet().removeIf(entry -> !duplicatesUUIDs.contains(entry.getKey()));
        log.debug("{} duplicates after cleanup step", duplicates.size());
        return duplicates;
    }

    @Transactional
    public Contact mergeContacts(User user, UUID targetContactUuid, List<ContactMergeRequest.MergeEntry> entries) {
        var targetContact = findContact(user, targetContactUuid);
        var contacts = Streams.stream(contactRepository.findAllById(entries.stream().map(ContactMergeRequest.MergeEntry::getUuid).toList()))
                .collect(Collectors.toMap(Contact::getUuid, Function.identity()));

        for (var entry : entries) {
            var contact = contacts.get(entry.getUuid());
            if (contact == null) {
                throw new ContactNotFoundException(entry.getUuid());
            }

            switch (entry.getMergeType()) {
                case FULL: mergeContact(contact, targetContact);
                case ONLY_DATA: mergeContactData(contact, targetContact);
            }

            deleteContact(user, contact);
        }

        return updateContact(user, targetContact);
    }

    private Contact updateContact(User user, Contact contact) {
        var afterSave = contactRepository.save(contact);
        log.info("contact {} saved", contact.getUuid());
        var audit = contactAuditRepository.save(createContactAudit(afterSave, user, AuditType.UPDATED));
        log.info("contact audit {} saved", audit.getUuid());
        return afterSave;
    }

    private void deleteContact(User user, Contact contact) {
        contact.setDeletedAt(Instant.now());
        var saved = contactRepository.save(contact);
        log.info("contact {} deleted", contact.getUuid());
        var audit = contactAuditRepository.save(createContactAudit(saved, user, AuditType.DELETED));
        log.info("contact audit {} saved", audit.getUuid());
    }

    private static Map<UUID, Set<UUID>> mapDuplicatesByUuid(Stream<Duplicate> duplicateStream) {
        return duplicateStream.collect(Collectors.groupingBy(
                Duplicate::getContactUuid,
                HashMap::new,
                Collectors.mapping(Duplicate::getDuplicateUuid, Collectors.toSet())));
    }

    private static void copyContact(Contact source, Contact target) {
        target.setName(source.getName());
        target.setAvatarUrl(source.getAvatarUrl());
        target.getData().clear();
        target.getData().addAll(source.getData());
        target.getData().forEach(data -> data.setContact(target));
    }

    private void validatePhoneNumber(User user, Contact contact) {
        contact.getData().stream()
                .filter(data -> data.getType().equals(ContactDataType.TELEPHONE))
                .forEach(data -> phoneValidationService.validatePhoneNumber(user, data.getValue()));
    }
  
    private static void mergeContact(Contact source, Contact target) {
        target.setName(source.getName());
        target.setAvatarUrl(source.getAvatarUrl());
        mergeContactData(source, target);
    }

    private static void mergeContactData(Contact contact, Contact target) {
        var notDuplicated = contact.getData().stream()
                .filter(data -> target.getData().stream().noneMatch(contactDataMatchPredicate(data)))
                .toList();
        notDuplicated.forEach(data -> data.setContact(target));
        contact.getData().removeIf(notDuplicated::contains);
        target.getData().addAll(notDuplicated);
    }

    private static Predicate<ContactData> contactDataMatchPredicate(ContactData data) {
        return targetData ->
                targetData.getType().equals(data.getType())
                && targetData.getValue().equals(data.getValue());
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
