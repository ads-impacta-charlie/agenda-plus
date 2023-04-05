package br.com.faculdadeimpacta.aluno.charlie.agendaplus.service;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.Contact;
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

    public List<Contact> listContacts() {
        log.info("requesting contact list");
        return contactRepository.findAll();
    }

    public Contact createContact(Contact contact) {
        log.info("creating new contact");
        return contactRepository.insert(contact);
    }

    public Contact findContact(UUID uuid) {
        log.info("finding a contact by uuid {}", uuid);
        return contactRepository.findById(uuid)
                .orElseThrow(() -> new ContactNotFoundException(uuid));
    }

    public Contact updateContact(UUID uuid, Contact contact) {
        log.info("updating contact {}", uuid);
        var stored = findContact(uuid);
        stored.setName(contact.getName());
        stored.getData().clear();
        stored.getData().addAll(contact.getData());
        return contactRepository.save(stored);
    }

    public void deleteContact(UUID uuid) {
        log.info("deleting contact {}", uuid);
        var contact = findContact(uuid);
        contactRepository.delete(contact);
    }
}
