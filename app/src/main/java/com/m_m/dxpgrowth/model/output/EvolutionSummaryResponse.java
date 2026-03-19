package com.m_m.dxpgrowth.model.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvolutionSummaryResponse {
    private UUID usuarioId;
    private Integer xpTotal;
    private Integer level;
    private Double progressToNextLevel;
    private Integer xpForCurrentLevel;
    private Integer xpForNextLevel;
    private Integer totalGoalsCreated;
    private Integer totalGoalsCompleted;
    private Integer totalGoalsCancelled;
    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDate lastActivityDate;
    private Integer goalsCompletedToday;
    private Integer goalsCompletedThisWeek;
    private Integer goalsCompletedThisMonth;
    private LocalDateTime dataAtualizacao;
}
