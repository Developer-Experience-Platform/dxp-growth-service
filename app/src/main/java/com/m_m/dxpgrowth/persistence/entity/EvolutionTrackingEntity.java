package com.m_m.dxpgrowth.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "evolution_tracking", indexes = {
    @Index(name = "idx_evolution_user", columnList = "usuario_id"),
    @Index(name = "idx_evolution_date", columnList = "data_registro")
})
public class EvolutionTrackingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "evolution_id", length = 36, nullable = false, unique = true)
    private UUID id;

    @Column(name = "usuario_id", length = 36, nullable = false)
    private UUID usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Column(name = "xp_earned", nullable = false)
    private Integer xpEarned;

    @Column(name = "xp_total", nullable = false)
    private Integer xpTotal;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "goal_reference_id")
    private UUID goalReferenceId;

    @Column(name = "goal_type_reference_id")
    private UUID goalTypeReferenceId;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;

    @PrePersist
    public void onCreate() {
        this.dataRegistro = LocalDateTime.now();
    }

    public enum ActionType {
        GOAL_CREATED(10),
        GOAL_COMPLETED(50),
        GOAL_CANCELLED(5),
        GOAL_UPDATED(2),
        GOAL_TYPE_CREATED(15),
        STREAK_BONUS(25),
        DAILY_LOGIN(5);

        private final int xpValue;

        ActionType(int xpValue) {
            this.xpValue = xpValue;
        }

        public int getXpValue() {
            return xpValue;
        }
    }
}
