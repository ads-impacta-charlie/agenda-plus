package br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "contact_data")
@AllArgsConstructor
@NoArgsConstructor
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
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_uuid", referencedColumnName = "contact_uuid")
    private Contact contact;
}
