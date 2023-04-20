package br.com.faculdadeimpacta.aluno.charlie.agendaplus.service;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.Contact;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.User;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception.ContactNotFoundException;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final ContactRepository contactRepository;

    public List<Contact> listContacts(User user) {
        log.info("requesting contact list for user {}", user);
        return contactRepository.findAllByUser(user);
    }

    public Contact createContact(User user, Contact contact) {
        log.info("creating new contact for user {}", user);
        contact.setUser(user);
        return contactRepository.insert(contact);
    }

    public Contact findContact(User user, UUID uuid) {
        log.info("finding a contact by uuid {} for user {}", uuid, user);
        return contactRepository.findByUuidAndUser(uuid, user)
                .orElseThrow(() -> new ContactNotFoundException(uuid));
    }

    public Contact updateContact(User user, UUID uuid, Contact contact) {
        log.info("updating contact {} for user {}", uuid, user);
        var stored = findContact(user, uuid);
        stored.setName(contact.getName());
        stored.setAvatarUrl(contact.getAvatarUrl());
        stored.getData().clear();
        stored.getData().addAll(contact.getData());
        return contactRepository.save(stored);
    }

    public void deleteContact(User user, UUID uuid) {
        log.info("deleting contact {} for user {}", uuid, user);
        var contact = findContact(user, uuid);
        contactRepository.delete(contact);
    }
}
