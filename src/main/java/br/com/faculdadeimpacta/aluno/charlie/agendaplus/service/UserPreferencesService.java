package br.com.faculdadeimpacta.aluno.charlie.agendaplus.service;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.AuditType;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.User;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.UserPreferences;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.UserPreferencesAudit;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception.PreferenceNotFoundException;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.UserPreferenceAuditRepository;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.UserPreferenceRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserPreferencesService {

    private final UserPreferenceRepository repository;
    private final UserPreferenceAuditRepository auditRepository;
    private final ObjectMapper objectMapper;

    private static void validatePreferences(User user, List<UserPreferences> preferences) {
        for (var preference : preferences) {
            if (!user.getPreferences().containsKey(preference.getKey())) {
                throw new PreferenceNotFoundException(preference.getKey());
            }
        }
    }

    @Transactional(readOnly = true)
    public List<UserPreferences> find(User user) {
        return repository.findAllByUser(user);
    }

    @Transactional
    public List<UserPreferences> createBatch(User user, List<UserPreferences> preferences) {
        log.info("creating batch preferences for user {}", user);
        var stored = Streams.stream(repository.saveAll(preferences.stream()
                .peek(preference -> preference.setUser(user))
                .peek(preference -> preference.setUuid(null))
                .toList())).toList();
        auditRepository.saveAll(stored.stream()
                .map(preference -> createAudit(preference, user, AuditType.CREATED))
                .toList());
        return stored;
    }

    @Transactional
    public List<UserPreferences> updateBatch(User user, List<UserPreferences> preferences) {
        log.info("updating batch preferences for user {}", user);

        validatePreferences(user, preferences);

        var preferenceMap = preferences.stream().collect(Collectors.toMap(UserPreferences::getKey, Function.identity()));
        var stored = repository
                .findAllByUserAndUuidIn(user, preferences.stream().map(UserPreferences::getUuid).toList())
                .stream()
                .peek(p -> {
                    var toUpdate = preferenceMap.get(p.getKey());
                    p.setValue(toUpdate.getValue());
                })
                .toList();

        stored = Streams.stream(repository.saveAll(stored)).toList();
        auditRepository.saveAll(stored.stream()
                .map(preference -> createAudit(preference, user, AuditType.UPDATED))
                .toList());
        return stored;
    }

    @Transactional
    public void deleteBatch(User user, List<UUID> preferencesUuids) {
        log.info("deleting batch preferences for user {}", user);

        var stored = repository.findAllByUserAndUuidIn(user, preferencesUuids);
        if (preferencesUuids.size() != stored.size()) {
            throw new PreferenceNotFoundException("");
        }

        stored.forEach(preference -> preference.setDeletedAt(Instant.now()));

        repository.saveAll(stored);
        auditRepository.saveAll(stored.stream()
                .map(preference -> createAudit(preference, user, AuditType.UPDATED))
                .toList());
    }

    private UserPreferencesAudit createAudit(UserPreferences preferences, User user, AuditType type) {
        try {
            return UserPreferencesAudit.builder()
                    .user(user)
                    .userPreference(preferences)
                    .updatedObject(objectMapper.writeValueAsString(preferences))
                    .auditType(type)
                    .occurredAt(Instant.now())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
