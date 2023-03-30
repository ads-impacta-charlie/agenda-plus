package br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.Contact;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ContactRepository extends CrudRepository<Contact, UUID> {
    List<Contact> findAll();

    default Contact insert(Contact contact) {
        contact.setUuid(null);
        return save(contact);
    }
}
