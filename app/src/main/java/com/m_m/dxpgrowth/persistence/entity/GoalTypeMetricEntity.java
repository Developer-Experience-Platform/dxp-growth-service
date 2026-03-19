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
@Table(name = "goal_type_metrics", indexes = {
    @Index(name = "idx_metrics_user_goal_type", columnList = "usuario_id, goal_type_id")
})
public class GoalTypeMetricEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "metric_id", length = 36, nullable = false, unique = true)
    private UUID id;

    @Column(name = "usuario_id", length = 36, nullable = false)
    private UUID usuarioId;

    @Column(name = "goal_type_id", length = 36, nullable = false)
    private UUID goalTypeId;

    @Column(name = "total_goals")
    private Integer totalGoals;

    @Column(name = "completed_goals")
    private Integer completedGoals;

    @Column(name = "cancelled_goals")
    private Integer cancelledGoals;

    @Column(name = "in_progress_goals")
    private Integer inProgressGoals;

    @Column(name = "completion_rate")
    private Double completionRate;

    @Column(name = "avg_completion_days")
    private Double avgCompletionDays;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @PrePersist
    public void onCreate() {
        if (this.totalGoals == null) this.totalGoals = 0;
        if (this.completedGoals == null) this.completedGoals = 0;
        if (this.cancelledGoals == null) this.cancelledGoals = 0;
        if (this.inProgressGoals == null) this.inProgressGoals = 0;
        if (this.completionRate == null) this.completionRate = 0.0;
        if (this.avgCompletionDays == null) this.avgCompletionDays = 0.0;
        this.dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
        if (this.totalGoals > 0) {
            this.completionRate = (double) this.completedGoals / this.totalGoals * 100;
        }
    }
}
