package br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.Contact;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ContactRepository extends CrudRepository<Contact, UUID>, JpaSpecificationExecutor<Contact> {
    default Contact insert(Contact contact) {
        contact.setUuid(null);
        if (contact.getData() != null) {
            contact.getData().forEach(data -> {
                data.setContact(contact);
                data.setUuid(null);
            });
        }
        return save(contact);
    }
}
