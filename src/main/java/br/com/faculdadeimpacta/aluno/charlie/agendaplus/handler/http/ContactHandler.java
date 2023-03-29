package br.com.faculdadeimpacta.aluno.charlie.agendaplus.handler.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@RestController
@RequestMapping("/contact")
@Slf4j
public class ContactHandler {

    @GetMapping
    public ResponseEntity<Object> list() {
        return ResponseEntity.internalServerError().body("not implemented");
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getDetails(@PathVariable("id") String id) {
        log.info("request getDetails id: {}", id);
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Object> create() {
        return ResponseEntity.internalServerError().body("not implemented");
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") String id) {
        log.info("request delete id: {}", id);
        return ResponseEntity.internalServerError().body("not implemented");
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> edit(@PathVariable("id") String id) {
        log.info("request edit id: {}", id);
        return ResponseEntity.internalServerError().body("not implemented");
    }

}
