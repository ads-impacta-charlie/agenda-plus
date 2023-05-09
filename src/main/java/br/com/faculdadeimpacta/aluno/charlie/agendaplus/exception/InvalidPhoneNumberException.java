package br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;

@Getter
public class InvalidPhoneNumberException extends RuntimeException {
    @JsonView(View.Public.class)
    private final String number;
    @JsonView(View.Public.class)
    private final String code;

    public InvalidPhoneNumberException(String number, String code) {
        this.number = number;
        this.code = code;
    }
}
