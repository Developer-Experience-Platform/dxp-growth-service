package com.m_m.dxpgrowth.service;

import com.m_m.dxpgrowth.model.output.EvolutionHistoryResponse;
import com.m_m.dxpgrowth.model.output.EvolutionSummaryResponse;
import com.m_m.dxpgrowth.model.output.GoalTypeMetricResponse;
import com.m_m.dxpgrowth.model.output.UserEvolutionResponse;
import com.m_m.dxpgrowth.persistence.entity.*;
import com.m_m.dxpgrowth.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvolutionService {

    private final EvolutionTrackingRepository trackingRepository;
    private final UserEvolutionSummaryRepository summaryRepository;
    private final GoalTypeMetricRepository metricRepository;
    private final GoalTypeRepository goalTypeRepository;
    private final GoalRepository goalRepository;

    @Transactional
    public void registerAction(UUID usuarioId, EvolutionTrackingEntity.ActionType actionType,
                              UUID goalReferenceId, UUID goalTypeReferenceId, String description) {
        int xpEarned = actionType.getXpValue();

        UserEvolutionSummaryEntity summary = summaryRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> createInitialSummary(usuarioId));

        int newXpTotal = summary.getXpTotal() + xpEarned;
        int newLevel = UserEvolutionSummaryEntity.calculateLevel(newXpTotal);

        summary.setXpTotal(newXpTotal);
        summary.setLevel(newLevel);
        summary.setLastActivityDate(LocalDate.now());

        updateCounters(summary, actionType);
        updateStreak(summary);
        summaryRepository.save(summary);

        EvolutionTrackingEntity tracking = new EvolutionTrackingEntity();
        tracking.setUsuarioId(usuarioId);
        tracking.setActionType(actionType);
        tracking.setXpEarned(xpEarned);
        tracking.setXpTotal(newXpTotal);
        tracking.setLevel(newLevel);
        tracking.setDescription(description);
        tracking.setGoalReferenceId(goalReferenceId);
        tracking.setGoalTypeReferenceId(goalTypeReferenceId);
        trackingRepository.save(tracking);

        if (goalTypeReferenceId != null) {
            updateGoalTypeMetrics(usuarioId, goalTypeReferenceId);
        }

        log.info("Registrada ação {} para usuário {} - XP: {}, Total: {}, Level: {}",
                actionType, usuarioId, xpEarned, newXpTotal, newLevel);
    }

    private UserEvolutionSummaryEntity createInitialSummary(UUID usuarioId) {
        UserEvolutionSummaryEntity summary = new UserEvolutionSummaryEntity();
        summary.setUsuarioId(usuarioId);
        return summary;
    }

    private void updateCounters(UserEvolutionSummaryEntity summary, EvolutionTrackingEntity.ActionType actionType) {
        switch (actionType) {
            case GOAL_CREATED -> summary.setTotalGoalsCreated(summary.getTotalGoalsCreated() + 1);
            case GOAL_COMPLETED -> {
                summary.setTotalGoalsCompleted(summary.getTotalGoalsCompleted() + 1);
                summary.setGoalsCompletedToday(summary.getGoalsCompletedToday() + 1);
                summary.setGoalsCompletedThisWeek(summary.getGoalsCompletedThisWeek() + 1);
                summary.setGoalsCompletedThisMonth(summary.getGoalsCompletedThisMonth() + 1);
            }
            case GOAL_CANCELLED -> summary.setTotalGoalsCancelled(summary.getTotalGoalsCancelled() + 1);
            default -> {}
        }
    }

    private void updateStreak(UserEvolutionSummaryEntity summary) {
        LocalDate today = LocalDate.now();
        LocalDate lastActivity = summary.getLastActivityDate();

        if (lastActivity == null) {
            summary.setCurrentStreak(1);
            summary.setLongestStreak(1);
        } else if (lastActivity.equals(today.minusDays(1))) {
            summary.setCurrentStreak(summary.getCurrentStreak() + 1);
            if (summary.getCurrentStreak() > summary.getLongestStreak()) {
                summary.setLongestStreak(summary.getCurrentStreak());
            }
        } else if (!lastActivity.equals(today)) {
            summary.setCurrentStreak(1);
        }

        if (summary.getCurrentStreak() > 1 && summary.getCurrentStreak() % 7 == 0) {
            registerAction(summary.getUsuarioId(), EvolutionTrackingEntity.ActionType.STREAK_BONUS,
                    null, null, "Bônus por sequência de " + summary.getCurrentStreak() + " dias!");
        }
    }

    private void updateGoalTypeMetrics(UUID usuarioId, UUID goalTypeId) {
        GoalTypeMetricEntity metrics = metricRepository.findByUsuarioIdAndGoalTypeId(usuarioId, goalTypeId)
                .orElseGet(() -> {
                    GoalTypeMetricEntity newMetrics = new GoalTypeMetricEntity();
                    newMetrics.setUsuarioId(usuarioId);
                    newMetrics.setGoalTypeId(goalTypeId);
                    return newMetrics;
                });

        List<GoalEntity> goals = goalRepository.findByUsuarioIdAndTipoId(usuarioId, goalTypeId);

        metrics.setTotalGoals(goals.size());
        metrics.setCompletedGoals((int) goals.stream()
                .filter(g -> g.getStatus() == StatusGoal.CONCLUIDO).count());
        metrics.setCancelledGoals((int) goals.stream()
                .filter(g -> g.getStatus() == StatusGoal.CANCELADO).count());
        metrics.setInProgressGoals((int) goals.stream()
                .filter(g -> g.getStatus() == StatusGoal.EM_ANDAMENTO).count());

        metricRepository.save(metrics);
    }

    public EvolutionSummaryResponse getEvolutionSummary(UUID usuarioId) {
        UserEvolutionSummaryEntity summary = summaryRepository.findByUsuarioId(usuarioId)
                .orElse(createDefaultSummary(usuarioId));

        return EvolutionSummaryResponse.builder()
                .usuarioId(summary.getUsuarioId())
                .xpTotal(summary.getXpTotal())
                .level(summary.getLevel())
                .progressToNextLevel(summary.getProgressToNextLevel())
                .xpForCurrentLevel(summary.getXpForCurrentLevel())
                .xpForNextLevel(summary.getXpForNextLevel())
                .totalGoalsCreated(summary.getTotalGoalsCreated())
                .totalGoalsCompleted(summary.getTotalGoalsCompleted())
                .totalGoalsCancelled(summary.getTotalGoalsCancelled())
                .currentStreak(summary.getCurrentStreak())
                .longestStreak(summary.getLongestStreak())
                .lastActivityDate(summary.getLastActivityDate())
                .goalsCompletedToday(summary.getGoalsCompletedToday())
                .goalsCompletedThisWeek(summary.getGoalsCompletedThisWeek())
                .goalsCompletedThisMonth(summary.getGoalsCompletedThisMonth())
                .dataAtualizacao(summary.getDataAtualizacao())
                .build();
    }

    private UserEvolutionSummaryEntity createDefaultSummary(UUID usuarioId) {
        UserEvolutionSummaryEntity summary = new UserEvolutionSummaryEntity();
        summary.setUsuarioId(usuarioId);
        summary.setXpTotal(0);
        summary.setLevel(1);
        return summary;
    }

    public List<EvolutionHistoryResponse> getEvolutionHistory(UUID usuarioId, int limit) {
        return trackingRepository.findByUsuarioIdOrderByDataRegistroDesc(usuarioId)
                .stream()
                .limit(limit)
                .map(this::toHistoryResponse)
                .toList();
    }

    private EvolutionHistoryResponse toHistoryResponse(EvolutionTrackingEntity tracking) {
        return EvolutionHistoryResponse.builder()
                .id(tracking.getId())
                .actionType(tracking.getActionType().name())
                .xpEarned(tracking.getXpEarned())
                .xpTotal(tracking.getXpTotal())
                .level(tracking.getLevel())
                .description(tracking.getDescription())
                .goalReferenceId(tracking.getGoalReferenceId())
                .goalTypeReferenceId(tracking.getGoalTypeReferenceId())
                .dataRegistro(tracking.getDataRegistro())
                .build();
    }

    public List<GoalTypeMetricResponse> getGoalTypeMetrics(UUID usuarioId) {
        return metricRepository.findByUsuarioIdOrderByCompletionRateDesc(usuarioId)
                .stream()
                .map(metric -> {
                    String goalTypeName = goalTypeRepository.findById(metric.getGoalTypeId())
                            .map(GoalTypeEntity::getNome)
                            .orElse("Desconhecido");

                    return GoalTypeMetricResponse.builder()
                            .id(metric.getId())
                            .usuarioId(metric.getUsuarioId())
                            .goalTypeId(metric.getGoalTypeId())
                            .goalTypeName(goalTypeName)
                            .totalGoals(metric.getTotalGoals())
                            .completedGoals(metric.getCompletedGoals())
                            .cancelledGoals(metric.getCancelledGoals())
                            .inProgressGoals(metric.getInProgressGoals())
                            .completionRate(metric.getCompletionRate())
                            .avgCompletionDays(metric.getAvgCompletionDays())
                            .build();
                })
                .toList();
    }

    public UserEvolutionResponse getFullEvolution(UUID usuarioId) {
        EvolutionSummaryResponse summary = getEvolutionSummary(usuarioId);
        List<EvolutionHistoryResponse> history = getEvolutionHistory(usuarioId, 20);
        List<GoalTypeMetricResponse> metrics = getGoalTypeMetrics(usuarioId);

        int xpCurrent = summary.getXpTotal() - summary.getXpForCurrentLevel();
        int xpRemaining = summary.getXpForNextLevel() - summary.getXpTotal();

        return UserEvolutionResponse.builder()
                .usuarioId(usuarioId)
                .summary(summary)
                .recentHistory(history)
                .goalTypeMetrics(metrics)
                .levelInfo(UserEvolutionResponse.LevelInfo.builder()
                        .currentLevel(summary.getLevel())
                        .xpCurrent(xpCurrent)
                        .xpForNextLevel(summary.getXpForNextLevel())
                        .xpRemaining(Math.max(0, xpRemaining))
                        .progressPercent(summary.getProgressToNextLevel())
                        .build())
                .build();
    }

    public EvolutionTrackingEntity.ActionType getActionTypeForGoalStatus(StatusGoal newStatus) {
        return switch (newStatus) {
            case EM_ANDAMENTO -> EvolutionTrackingEntity.ActionType.GOAL_UPDATED;
            case CONCLUIDO -> EvolutionTrackingEntity.ActionType.GOAL_COMPLETED;
            case CANCELADO -> EvolutionTrackingEntity.ActionType.GOAL_CANCELLED;
            default -> EvolutionTrackingEntity.ActionType.GOAL_UPDATED;
        };
    }
}
