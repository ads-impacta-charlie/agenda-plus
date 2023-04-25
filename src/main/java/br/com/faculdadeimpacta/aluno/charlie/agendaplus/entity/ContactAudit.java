package br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.JsonJdbcType;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "contact_audit")
@AllArgsConstructor
@NoArgsConstructor
public class ContactAudit {
    /** {@link UUID} for the audit log entry */
    @Id
    @Column(name = "contact_audit_uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    /** {@link AuditType} representing the operation that was performed */
    @Enumerated(EnumType.STRING)
    @NotNull
    private AuditType auditType;

    /** {@link User} that performed the action */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_uuid")
    private User user;

    /** JSON representation for the object <em>AFTER</em> the operation */
    @JdbcType(JsonJdbcType.class)
    @NotNull
    private String updatedObject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contact_uuid")
    @ToString.Exclude
    private Contact contact;

    @NotNull
    private Instant occurredAt;
}
