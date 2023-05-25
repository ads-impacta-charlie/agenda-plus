package br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "contact")
@Validated
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Contact {
    @Id
    @Column(name = "contact_uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID uuid;

    @Size(min = 3, message = "Name must have at least 3 characters")
    private String name;

    private String avatarUrl;

    private Boolean favorite;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "contact", orphanRemoval = true)
    @Valid
    private List<ContactData> data;

    @Nullable
    @JsonIgnore
    private Instant deletedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_uuid")
    @JsonIgnore
    private User user;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contact")
    private List<ContactAudit> auditTrail;
}
