package com.m_m.dxpgrowth.model.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalTypeMetricResponse {
    private UUID id;
    private UUID usuarioId;
    private UUID goalTypeId;
    private String goalTypeName;
    private Integer totalGoals;
    private Integer completedGoals;
    private Integer cancelledGoals;
    private Integer inProgressGoals;
    private Double completionRate;
    private Double avgCompletionDays;
}
