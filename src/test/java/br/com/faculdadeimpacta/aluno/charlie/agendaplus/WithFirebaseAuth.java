package br.com.faculdadeimpacta.aluno.charlie.agendaplus;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithFirebaseAuthSecurityContextFactory.class)
public @interface WithFirebaseAuth  {
    String firebaseUserId() default "test-user-id";
}
