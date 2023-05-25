package br.com.faculdadeimpacta.aluno.charlie.agendaplus.handler.http;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.Contact;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.ContactMergeRequest;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.User;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.View;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception.ContactNotFoundException;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception.InvalidPhoneNumberException;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.service.ContactService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@RestController
@RequestMapping(
        value = "/contact",
        produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
@Secured({"ROLE_USER"})
@SecurityRequirement(name = "security_auth")
public class ContactHandler {

    private final ContactService contactService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            description = "List all contacts",
            summary = "List all contacts")
    public List<Contact> list(User user) {
        log.info("request list");
        return contactService.listContacts(user);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            description = "Get details for a single contact",
            summary = "Get details for a single contact")
    public Contact getDetails(User user, @PathVariable("id") UUID id) {
        log.info("request getDetails id: {}", id);
        return contactService.findContact(user, id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            description = "Create a new contact",
            summary = "Create a new contact")
    public Contact create(User user, @Valid @RequestBody Contact contact) {
        log.info("request create {}", contact);
        return contactService.createContact(user, contact);
    }

    @PostMapping(path = "/bulk", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            description = "Create new contacts in bulk",
            summary = "Create new contacts in bulk")
    public List<Contact> create(User user, @Valid @RequestBody List<Contact> contacts) {
        log.info("request create in bulk");
        return contactService.createContactBulk(user, contacts);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            description = "Delete an existing contact",
            summary = "Delete an existing contact")
    public void delete(User user, @PathVariable("id") UUID id) {
        log.info("request delete id: {}", id);
        contactService.deleteContact(user, id);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            description = "Edit an existing contact",
            summary = "Edit an existing contact")
    public Contact edit(User user, @PathVariable("id") UUID id, @RequestBody Contact contact) {
        log.info("request edit id: {}; contact {}", id, contact);
        return contactService.updateContact(user, id, contact);
    }

    @PutMapping(path = "/{id}/favorite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setFavorite(User user, @PathVariable("id") UUID id, @RequestParam("favorite") Boolean favorite) {
        log.info("request set contact as favorite: {} {}", id, favorite);
        contactService.setContactAsFavorite(user, id, favorite);
    }

    @GetMapping(path = "/duplicates")
    @Operation(
            description = "Finds all duplicates by their name or contact data",
            summary = "Finds all duplicates by their name or contact data")
    public Map<UUID, Set<UUID>> findDuplicates(User user) {
        log.info("request find duplicates");
        return contactService.findDuplicates(user);
    }

    @PostMapping(path = "/merge/{id}")
    @Operation(
            description = "Merge the given contacts from the body into the contact given as parameter",
            summary = "Merge the given contacts from the body into the contact given as parameter")
    public Contact merge(User user, @PathVariable("id") UUID id, @RequestBody ContactMergeRequest contactMergeRequest) {
        log.info("request merge contacts into {}", id);
        return contactService.mergeContacts(user, id, contactMergeRequest.getEntries());
    }

    @ExceptionHandler(ContactNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @JsonView(View.Public.class)
    public ContactNotFoundException handleContactNotFoundException(ContactNotFoundException e) {
        return e;
    }

    @ExceptionHandler(InvalidPhoneNumberException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @JsonView(View.Public.class)
    public InvalidPhoneNumberException handleInvalidPhoneNumberException(InvalidPhoneNumberException e) {
        return e;
    }
}
