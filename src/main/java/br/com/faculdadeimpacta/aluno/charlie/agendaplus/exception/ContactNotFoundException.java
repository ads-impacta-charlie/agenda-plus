package br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ContactNotFoundException extends RuntimeException {
    @JsonView(View.Public.class)
    private final UUID uuid;
    @JsonView(View.Public.class)
    private final String code;

    public ContactNotFoundException(UUID uuid) {
        code = "not_found";
        this.uuid = uuid;
    }
}
