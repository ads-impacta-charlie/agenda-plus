package br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.ContactAudit;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ContactAuditRepository extends CrudRepository<ContactAudit, UUID> {
}
