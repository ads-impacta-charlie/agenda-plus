package br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.Contact;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ContactSpecifications {
    public static Specification<Contact> notDeleted() {
        return (root, query, criteria) -> criteria.isNull(root.get("deletedAt"));
    }

    public static Specification<Contact> withId(UUID id) {
        return (root, query, criteria) -> criteria.equal(root.get("uuid"), id);
    }

    public static Specification<Contact> forUser(User user) {
        return (root, query, criteria) -> criteria.equal(root.get("user"), user);
    }
}
