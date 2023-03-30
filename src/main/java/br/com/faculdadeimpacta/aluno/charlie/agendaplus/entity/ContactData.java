package br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "contact_data")
public class ContactData {
    @Id
    @Column(name = "contact_data_uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    @Enumerated(EnumType.STRING)
    @NotNull
    private ContactDataType type;
    @Enumerated(EnumType.STRING)
    @NotNull
    private ContactDataCategory category;
    @Column(name = "contact_value")
    @NotBlank
    private String value;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    private Contact contact;
}
