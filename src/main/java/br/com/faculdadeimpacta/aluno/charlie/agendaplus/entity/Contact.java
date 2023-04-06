package br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "contact")
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    @Id
    @Column(name = "contact_uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    @Size(min = 3, message = "Name must have at least 3 characters")
    private String name;
    private String avatarUrl;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "contact_uuid", referencedColumnName = "contact_uuid", nullable = false)
    @Valid
    private List<ContactData> data;
}
