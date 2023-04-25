package br.com.faculdadeimpacta.aluno.charlie.agendaplus.service;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.User;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository repository;
    @InjectMocks
    UserService service;
    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void shouldCallRepository() {
        var user = new User();
        doReturn(Optional.of(user))
                .when(repository)
                .findByFirebaseUserId(any());

        var got = service.findOrCreateUserByFirebaseUserId("a");

        verify(repository).findByFirebaseUserId(eq("a"));
        assertThat(got).isEqualTo(user);
    }

    @Test
    void shouldCreateUserWhenNotFound() {
        doReturn(Optional.empty())
                .when(repository)
                .findByFirebaseUserId(any());

        service.findOrCreateUserByFirebaseUserId("firebase user identifier");

        verify(repository).save(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue())
                .isNotNull()
                .extracting(User::getFirebaseUserId)
                .isEqualTo("firebase user identifier");
    }

    @Test
    void shouldGetFirebaseIdFromSecurityContext() {
        var user = new User();
        doReturn(Optional.of(user))
                .when(repository)
                .findByFirebaseUserId(any());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        var jwt = Jwt.withTokenValue("aaa")
                .header("alg", "HS256")
                .header("typ", "JWT")
                .claim("iat", Instant.now().minusSeconds(10))
                .claim("exp", Instant.now().plus(5, ChronoUnit.DAYS))
                .subject("token subject")
                .build();
        var authority = new SimpleGrantedAuthority("ROLE_USER");
        var auth = new JwtAuthenticationToken(jwt, List.of(authority), "token subject");
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        var got = service.getCurrentUser();

        verify(repository).findByFirebaseUserId(eq("token subject"));
        assertThat(got).isEqualTo(user);
    }
}