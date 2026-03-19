package com.m_m.dxpgrowth.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "goal_db")
public class GoalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "goal_id", length = 36, nullable = false, unique = true)
    private UUID id;

    @Column(name = "usuario_id", length = 36, nullable = false)
    private UUID usuarioId;

    @Column(name = "titulo_goal", length = 255, nullable = false)
    private String titulo;

    @Column(name = "descricao_goal", length = 1000)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_type_id")
    private GoalTypeEntity tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_goal", nullable = false)
    private StatusGoal status;

    @Column(name = "prazo_goal")
    private LocalDate prazo;

    @Column(name = "data_criacao_goal", nullable = false)
    private LocalDateTime dataHoraCriacao;

    @Column(name = "data_atualizacao_goal")
    private LocalDateTime dataHoraAtualizacao;

    @PrePersist
    public void onCreate() {
        this.dataHoraCriacao = LocalDateTime.now();
        this.dataHoraAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.dataHoraAtualizacao = LocalDateTime.now();
    }
}
