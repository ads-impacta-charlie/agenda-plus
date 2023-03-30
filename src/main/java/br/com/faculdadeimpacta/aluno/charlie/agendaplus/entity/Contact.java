package br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "contact")
@Validated
public class Contact {
    @Id
    @Column(name = "contact_uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    @Size(min = 3, message = "Name must have at least 3 characters")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "contact_uuid", referencedColumnName = "contact_uuid", nullable = false)
    @Valid
    private List<ContactData> data;
}