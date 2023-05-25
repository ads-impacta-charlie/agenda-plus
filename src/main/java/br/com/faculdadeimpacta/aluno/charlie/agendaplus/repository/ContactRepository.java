package br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.Contact;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.Duplicate;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface ContactRepository extends CrudRepository<Contact, UUID>, JpaSpecificationExecutor<Contact> {
    @Query("""
            SELECT
                c.uuid AS contactUuid
                , c2.uuid AS duplicateUuid
            FROM Contact c
                 LEFT JOIN Contact c2 ON c.name = c2.name AND c.uuid != c2.uuid
            WHERE
                c.user = :user
                AND c2.user = :user
                AND c.deletedAt IS NULL
                AND c2.deletedAt IS NULL
            """)
    Stream<Duplicate> findDuplicatesByName(User user);

    @Query("""
    SELECT
        c.uuid AS contactUuid
        , c2.uuid AS duplicateUuid
            FROM Contact c
                 INNER JOIN ContactData cd ON cd.contact = c
                 LEFT JOIN ContactData cd2 ON
                        cd.uuid != cd2.uuid
                        AND cd2.value = cd.value
                        AND cd2.type = cd.type
                 LEFT JOIN Contact c2 ON c2 = cd2.contact
            WHERE
                c.user = :user
                AND c2.user = :user
                AND c.deletedAt IS NULL
                AND c2.deletedAt IS NULL
            """)
    Stream<Duplicate> findDuplicatesByContactData(User user);

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

    default List<Contact> insertAll(List<Contact> contacts) {
        contacts.forEach(contact -> {
            contact.setUuid(null);
            if (contact.getData() != null) {
                contact.getData().forEach(data -> {
                    data.setContact(contact);
                    data.setUuid(null);
                });
            }
        });
        var savedContacts = new ArrayList<Contact>(contacts.size());
        saveAll(contacts).forEach(savedContacts::add);
        return savedContacts;
    }
}
