package br.com.faculdadeimpacta.aluno.charlie.agendaplus.service;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.Contact;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
