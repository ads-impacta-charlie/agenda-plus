package br.com.faculdadeimpacta.aluno.charlie.agendaplus;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class WithFirebaseAuthSecurityContextFactory implements WithSecurityContextFactory<WithFirebaseAuth> {
    @Override
    public SecurityContext createSecurityContext(WithFirebaseAuth annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        var jwt = Jwt.withTokenValue("aaa")
                .header("alg", "HS256")
                .header("typ", "JWT")
                .claim("iat", Instant.now().minusSeconds(10))
                .claim("exp", Instant.now().plus(5, ChronoUnit.DAYS))
                .subject(annotation.firebaseUserId())
                .build();
        var authority = new SimpleGrantedAuthority("ROLE_USER");
//        var authority2 = new OAuth2UserAuthority("ROLE_User", Collections.singletonMap("sub", annotation.firebaseUserId()));
        var auth = new JwtAuthenticationToken(jwt, List.of(authority), annotation.firebaseUserId());
        context.setAuthentication(auth);
        return context;
    }
}
