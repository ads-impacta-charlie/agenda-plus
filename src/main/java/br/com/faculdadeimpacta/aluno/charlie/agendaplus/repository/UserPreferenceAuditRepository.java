package br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.UserPreferencesAudit;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserPreferenceAuditRepository extends CrudRepository<UserPreferencesAudit, UUID> {
}
