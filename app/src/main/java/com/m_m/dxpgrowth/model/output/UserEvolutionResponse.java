package com.m_m.dxpgrowth.model.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvolutionResponse {
    private UUID usuarioId;
    private EvolutionSummaryResponse summary;
    private List<EvolutionHistoryResponse> recentHistory;
    private List<GoalTypeMetricResponse> goalTypeMetrics;
    private LevelInfo levelInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelInfo {
        private Integer currentLevel;
        private Integer xpCurrent;
        private Integer xpForNextLevel;
        private Integer xpRemaining;
        private Double progressPercent;
    }
}
