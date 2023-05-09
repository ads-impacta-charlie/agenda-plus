package br.com.faculdadeimpacta.aluno.charlie.agendaplus.handler.http;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.User;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.UserPreferences;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.View;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception.PreferenceNotFoundException;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.service.UserPreferencesService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Component
@RestController
@RequestMapping(
        value = "/preferences",
        produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
@Secured({"ROLE_USER"})
@SecurityRequirement(name = "security_auth")
public class UserPreferencesHandler {

    private final UserPreferencesService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @JsonView(View.Public.class)
    @Operation(
            description = "List all preferences",
            summary = "List all preferences")
    public List<UserPreferences> list(User user) {
        log.info("request list for user {}", user);
        return service.find(user);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @JsonView(View.Public.class)
    @Operation(
            description = "Create a batch of preferences",
            summary = "Create a batch of preferences")
    public List<UserPreferences> createBatch(
            User user,
            @JsonView(View.Public.class) @Validated @RequestBody List<UserPreferences> preferences) {
        log.info("request create batch for user {}", user);
        return service.createBatch(user, preferences);
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            description = "Delete existing preferences",
            summary = "Delete existing preferences")
    public void delete(User user, @RequestBody List<UUID> uuidList) {
        log.info("request delete batch for user {}", user);
        service.deleteBatch(user, uuidList);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @JsonView(View.Public.class)
    @Operation(
            description = "Edit existing preferences",
            summary = "Edit existing preferences")
    public List<UserPreferences> edit(User user, @JsonView(View.Public.class) @Validated @RequestBody List<UserPreferences> preferences) {
        log.info("request edit batch for user {}", user);
        return service.updateBatch(user, preferences);
    }

    @ExceptionHandler(PreferenceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @JsonView(View.Public.class)
    public PreferenceNotFoundException handlePreferenceNotFoundException(PreferenceNotFoundException e) {
        return e;
    }

}
