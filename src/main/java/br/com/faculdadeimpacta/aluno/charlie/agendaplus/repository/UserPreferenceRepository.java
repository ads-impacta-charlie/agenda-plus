package br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.User;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.UserPreferences;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface UserPreferenceRepository extends CrudRepository<UserPreferences, UUID> {
    List<UserPreferences> findAllByUser(User user);

    List<UserPreferences> findAllByUserAndUuidIn(User user, List<UUID> uuids);
}
