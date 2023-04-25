package br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.Contact;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactRepository extends CrudRepository<Contact, UUID> {
    List<Contact> findAllByUser(User user);
    Optional<Contact> findByUuidAndUser(UUID uuid, User user);

    default Contact insert(Contact contact) {
        contact.setUuid(null);
        return save(contact);
    }
}
