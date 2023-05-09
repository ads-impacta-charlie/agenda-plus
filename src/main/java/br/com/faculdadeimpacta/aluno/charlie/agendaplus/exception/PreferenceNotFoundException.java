package br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;

@Getter
public class PreferenceNotFoundException extends RuntimeException {
    @JsonView(View.Public.class)
    private final String key;
    @JsonView(View.Public.class)
    private final String code;

    public PreferenceNotFoundException(String key) {
        code = "not_found";
        this.key = key;
    }
}
