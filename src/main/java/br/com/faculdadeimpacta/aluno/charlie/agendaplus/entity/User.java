package br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@Entity
@Table(name = "user", indexes = {@Index(unique = true, columnList = "firebase_user_id")})
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "user_uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    @Column(name = "firebase_user_id")
    @NotNull
    private String firebaseUserId;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", referencedColumnName = "user_uuid", nullable = false, insertable = false, updatable = false)
    private List<Contact> contacts;

    @Override
    public String toString() {
        return Optional.ofNullable(uuid)
                .map(Object::toString)
                .orElse("<nil uuid>");
    }
}
