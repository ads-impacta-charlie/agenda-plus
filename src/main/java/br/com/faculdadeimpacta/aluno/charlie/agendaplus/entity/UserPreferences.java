package br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
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
    @JsonView(View.Public.class)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_uuid")
    @JsonIgnore
    private User user;

    @NotNull
    @Column(name = "preference_key")
    @JsonView(View.Public.class)
    private String key;

    @NotNull
    @Column(name = "preference_value")
    @JsonView(View.Public.class)
    private String value;

    @Nullable
    @JsonIgnore
    private Instant deletedAt;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userPreference")
    private List<UserPreferencesAudit> auditTrail;
}
