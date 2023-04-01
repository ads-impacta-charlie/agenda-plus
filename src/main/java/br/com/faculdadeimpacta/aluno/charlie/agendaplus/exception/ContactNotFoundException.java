package br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception;

import lombok.Getter;

import java.util.UUID;

@Getter

public class ContactNotFoundException extends RuntimeException {
    private final UUID uuid;
    private final String code;

    public ContactNotFoundException(UUID uuid) {
        code = "not_found";
        this.uuid = uuid;
    }
}
