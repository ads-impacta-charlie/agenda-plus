package br.com.faculdadeimpacta.aluno.charlie.agendaplus.service;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.User;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @NotNull
    public User findOrCreateUserByFirebaseUserId(String firebaseUserId) {
        return userRepository.findByFirebaseUserId(firebaseUserId)
                .orElseGet(() -> {
                    log.info("user {} not found, creating new", firebaseUserId);
                    var user = new User();
                    user.setFirebaseUserId(firebaseUserId);
                    return userRepository.save(user);
                });
    }
}
