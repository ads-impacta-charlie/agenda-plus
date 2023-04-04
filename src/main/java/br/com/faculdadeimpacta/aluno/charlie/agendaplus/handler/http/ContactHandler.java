package br.com.faculdadeimpacta.aluno.charlie.agendaplus.handler.http;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.Contact;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception.ContactNotFoundException;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Component
@RestController
@RequestMapping("/contact")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class ContactHandler {

    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<List<Contact>> list() {
        log.info("request list");
        var contacts = contactService.listContacts();
        return ResponseEntity.ok(contacts);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Contact> getDetails(@PathVariable("id") UUID id) {
        log.info("request getDetails id: {}", id);
        var contact = contactService.findContact(id);
        return ResponseEntity.ok(contact);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Contact contact) {
        log.info("request create {}", contact);
        try {
            var savedContact = contactService.createContact(contact);
            return ResponseEntity.ok(savedContact);
        } catch (Exception e) {
            log.error("exception", e);
            return ResponseEntity.unprocessableEntity().body(e);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") UUID id) {
        log.info("request delete id: {}", id);
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Contact> edit(@PathVariable("id") UUID id, @RequestBody Contact contact) {
        log.info("request edit id: {}; contact {}", id, contact);
        var updatedContact = contactService.updateContact(id, contact);
        return ResponseEntity.ok(updatedContact);
    }

    @ExceptionHandler(ContactNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ContactNotFoundException handleContactNotFoundException(ContactNotFoundException e) {
        return e;
    }

}
