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
@Table(name = "user_evolution_summary", indexes = {
    @Index(name = "idx_summary_user", columnList = "usuario_id", unique = true)
})
public class UserEvolutionSummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "summary_id", length = 36, nullable = false, unique = true)
    private UUID id;

    @Column(name = "usuario_id", length = 36, nullable = false, unique = true)
    private UUID usuarioId;

    @Column(name = "xp_total", nullable = false)
    private Integer xpTotal;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "total_goals_created")
    private Integer totalGoalsCreated;

    @Column(name = "total_goals_completed")
    private Integer totalGoalsCompleted;

    @Column(name = "total_goals_cancelled")
    private Integer totalGoalsCancelled;

    @Column(name = "current_streak")
    private Integer currentStreak;

    @Column(name = "longest_streak")
    private Integer longestStreak;

    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;

    @Column(name = "goals_completed_today")
    private Integer goalsCompletedToday;

    @Column(name = "goals_completed_this_week")
    private Integer goalsCompletedThisWeek;

    @Column(name = "goals_completed_this_month")
    private Integer goalsCompletedThisMonth;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @PrePersist
    public void onCreate() {
        if (this.xpTotal == null) this.xpTotal = 0;
        if (this.level == null) this.level = 1;
        if (this.totalGoalsCreated == null) this.totalGoalsCreated = 0;
        if (this.totalGoalsCompleted == null) this.totalGoalsCompleted = 0;
        if (this.totalGoalsCancelled == null) this.totalGoalsCancelled = 0;
        if (this.currentStreak == null) this.currentStreak = 0;
        if (this.longestStreak == null) this.longestStreak = 0;
        if (this.goalsCompletedToday == null) this.goalsCompletedToday = 0;
        if (this.goalsCompletedThisWeek == null) this.goalsCompletedThisWeek = 0;
        if (this.goalsCompletedThisMonth == null) this.goalsCompletedThisMonth = 0;
        this.dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public static int calculateLevel(int xp) {
        return (int) Math.floor((Math.sqrt(2 * xp / 100) + 1));
    }

    public static int xpForNextLevel(int level) {
        return 100 * level * (level + 1) / 2;
    }

    public int getXpForCurrentLevel() {
        return xpForNextLevel(this.level - 1);
    }

    public int getXpForNextLevel() {
        return xpForNextLevel(this.level);
    }

    public double getProgressToNextLevel() {
        int currentLevelXp = getXpForCurrentLevel();
        int nextLevelXp = getXpForNextLevel();
        int xpInLevel = this.xpTotal - currentLevelXp;
        int xpNeeded = nextLevelXp - currentLevelXp;
        return (double) xpInLevel / xpNeeded * 100;
    }
}
