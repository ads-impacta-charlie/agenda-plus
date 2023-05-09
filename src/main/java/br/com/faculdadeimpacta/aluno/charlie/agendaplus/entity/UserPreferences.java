package br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "user_preferences")
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    @Id
    @Column(name = "user_preference_uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_uuid")
    @JsonIgnore
    private User user;

    @NotNull
    @Column(name = "preference_key")
    private String key;

    @NotNull
    @Column(name = "preference_value")
    private String value;
}
