package com.m_m.dxpgrowth.service;

import com.m_m.dxpgrowth.model.output.EvolutionHistoryResponse;
import com.m_m.dxpgrowth.model.output.EvolutionSummaryResponse;
import com.m_m.dxpgrowth.model.output.GoalTypeMetricResponse;
import com.m_m.dxpgrowth.model.output.UserEvolutionResponse;
import com.m_m.dxpgrowth.persistence.entity.*;
import com.m_m.dxpgrowth.persistence.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EvolutionServiceTest {

    @Mock
    private EvolutionTrackingRepository trackingRepository;

    @Mock
    private UserEvolutionSummaryRepository summaryRepository;

    @Mock
    private GoalTypeMetricRepository metricRepository;

    @Mock
    private GoalTypeRepository goalTypeRepository;

    @Mock
    private GoalRepository goalRepository;

    private EvolutionService evolutionService;

    private final UUID USER_ID = UUID.randomUUID();
    private final UUID GOAL_ID = UUID.randomUUID();
    private final UUID GOAL_TYPE_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        evolutionService = new EvolutionService(
                trackingRepository,
                summaryRepository,
                metricRepository,
                goalTypeRepository,
                goalRepository
        );
    }

    @Test
    void registerAction_WithNewUser_ShouldCreateInitialSummary() {
        when(summaryRepository.findByUsuarioId(USER_ID)).thenReturn(Optional.empty());
        when(summaryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(trackingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        evolutionService.registerAction(
                USER_ID,
                EvolutionTrackingEntity.ActionType.GOAL_CREATED,
                GOAL_ID,
                GOAL_TYPE_ID,
                "Meta criada"
        );

        verify(summaryRepository, times(1)).save(any(UserEvolutionSummaryEntity.class));

        ArgumentCaptor<EvolutionTrackingEntity> captor = ArgumentCaptor.forClass(EvolutionTrackingEntity.class);
        verify(trackingRepository, times(1)).save(captor.capture());

        EvolutionTrackingEntity saved = captor.getValue();
        assertEquals(USER_ID, saved.getUsuarioId());
        assertEquals(EvolutionTrackingEntity.ActionType.GOAL_CREATED, saved.getActionType());
        assertEquals(10, saved.getXpEarned());
        assertEquals(10, saved.getXpTotal());
        assertEquals(1, saved.getLevel());
    }

    @Test
    void registerAction_WithExistingUser_ShouldUpdateSummary() {
        UserEvolutionSummaryEntity existingSummary = createMockSummary(100, 3);
        when(summaryRepository.findByUsuarioId(USER_ID)).thenReturn(Optional.of(existingSummary));
        when(summaryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(trackingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        evolutionService.registerAction(
                USER_ID,
                EvolutionTrackingEntity.ActionType.GOAL_COMPLETED,
                GOAL_ID,
                GOAL_TYPE_ID,
                "Meta concluída"
        );

        verify(summaryRepository, times(1)).save(existingSummary);
        assertEquals(150, existingSummary.getXpTotal());
        assertEquals(4, existingSummary.getTotalGoalsCompleted());
    }

    @Test
    void registerAction_ForGoalCompleted_ShouldIncrementStreak() {
        UserEvolutionSummaryEntity existingSummary = createMockSummary(100, 3);
        existingSummary.setLastActivityDate(LocalDate.now().minusDays(1));
        existingSummary.setCurrentStreak(5);

        when(summaryRepository.findByUsuarioId(USER_ID)).thenReturn(Optional.of(existingSummary));
        when(summaryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        evolutionService.registerAction(
                USER_ID,
                EvolutionTrackingEntity.ActionType.GOAL_COMPLETED,
                GOAL_ID,
                GOAL_TYPE_ID,
                "Meta concluída"
        );

        assertEquals(6, existingSummary.getCurrentStreak());
        assertEquals(1, existingSummary.getGoalsCompletedToday());
    }

    @Test
    void getEvolutionSummary_WithExistingUser_ShouldReturnSummary() {
        UserEvolutionSummaryEntity existingSummary = createMockSummary(250, 5);
        existingSummary.setCurrentStreak(7);
        existingSummary.setLongestStreak(14);
        existingSummary.setLastActivityDate(LocalDate.now());

        when(summaryRepository.findByUsuarioId(USER_ID)).thenReturn(Optional.of(existingSummary));

        EvolutionSummaryResponse summary = evolutionService.getEvolutionSummary(USER_ID);

        assertNotNull(summary);
        assertEquals(USER_ID, summary.getUsuarioId());
        assertEquals(250, summary.getXpTotal());
        assertEquals(5, summary.getLevel());
        assertEquals(7, summary.getCurrentStreak());
    }

    @Test
    void getEvolutionSummary_WithNewUser_ShouldReturnDefaultSummary() {
        when(summaryRepository.findByUsuarioId(USER_ID)).thenReturn(Optional.empty());

        EvolutionSummaryResponse summary = evolutionService.getEvolutionSummary(USER_ID);

        assertNotNull(summary);
        assertEquals(USER_ID, summary.getUsuarioId());
        assertEquals(0, summary.getXpTotal());
        assertEquals(1, summary.getLevel());
    }

    @Test
    void getEvolutionHistory_ShouldReturnLimitedHistory() {
        List<EvolutionTrackingEntity> history = List.of(
                createMockTracking(EvolutionTrackingEntity.ActionType.GOAL_CREATED, 10),
                createMockTracking(EvolutionTrackingEntity.ActionType.GOAL_COMPLETED, 50)
        );

        when(trackingRepository.findByUsuarioIdOrderByDataRegistroDesc(USER_ID)).thenReturn(history);

        List<EvolutionHistoryResponse> result = evolutionService.getEvolutionHistory(USER_ID, 10);

        assertEquals(2, result.size());
        assertEquals("GOAL_CREATED", result.get(0).getActionType());
        assertEquals("GOAL_COMPLETED", result.get(1).getActionType());
    }

    @Test
    void getGoalTypeMetrics_ShouldReturnMetricsWithGoalTypeName() {
        GoalTypeMetricEntity metric = new GoalTypeMetricEntity();
        metric.setGoalTypeId(GOAL_TYPE_ID);
        metric.setTotalGoals(10);
        metric.setCompletedGoals(7);
        metric.setCompletionRate(70.0);

        GoalTypeEntity goalType = new GoalTypeEntity();
        goalType.setNome("Programação");

        when(metricRepository.findByUsuarioIdOrderByCompletionRateDesc(USER_ID))
                .thenReturn(List.of(metric));
        when(goalTypeRepository.findById(GOAL_TYPE_ID)).thenReturn(Optional.of(goalType));

        List<GoalTypeMetricResponse> result = evolutionService.getGoalTypeMetrics(USER_ID);

        assertEquals(1, result.size());
        assertEquals("Programação", result.get(0).getGoalTypeName());
        assertEquals(70.0, result.get(0).getCompletionRate());
    }

    @Test
    void getFullEvolution_ShouldReturnCompleteEvolution() {
        UserEvolutionSummaryEntity summary = createMockSummary(250, 5);
        List<EvolutionTrackingEntity> history = List.of(
                createMockTracking(EvolutionTrackingEntity.ActionType.GOAL_CREATED, 10)
        );

        when(summaryRepository.findByUsuarioId(USER_ID)).thenReturn(Optional.of(summary));
        when(trackingRepository.findByUsuarioIdOrderByDataRegistroDesc(USER_ID)).thenReturn(history);
        when(metricRepository.findByUsuarioIdOrderByCompletionRateDesc(USER_ID)).thenReturn(List.of());
        when(goalTypeRepository.findById(any())).thenReturn(Optional.empty());

        UserEvolutionResponse result = evolutionService.getFullEvolution(USER_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.getUsuarioId());
        assertNotNull(result.getSummary());
        assertNotNull(result.getLevelInfo());
        assertEquals(1, result.getRecentHistory().size());
    }

    @Test
    void getActionTypeForGoalStatus_ShouldReturnCorrectActionType() {
        assertEquals(EvolutionTrackingEntity.ActionType.GOAL_UPDATED,
                evolutionService.getActionTypeForGoalStatus(StatusGoal.EM_ANDAMENTO));
        assertEquals(EvolutionTrackingEntity.ActionType.GOAL_COMPLETED,
                evolutionService.getActionTypeForGoalStatus(StatusGoal.CONCLUIDO));
        assertEquals(EvolutionTrackingEntity.ActionType.GOAL_CANCELLED,
                evolutionService.getActionTypeForGoalStatus(StatusGoal.CANCELADO));
    }

    private UserEvolutionSummaryEntity createMockSummary(int xpTotal, int level) {
        UserEvolutionSummaryEntity summary = new UserEvolutionSummaryEntity();
        summary.setUsuarioId(USER_ID);
        summary.setXpTotal(xpTotal);
        summary.setLevel(level);
        summary.setTotalGoalsCreated(5);
        summary.setTotalGoalsCompleted(3);
        summary.setTotalGoalsCancelled(1);
        summary.setCurrentStreak(0);
        summary.setLongestStreak(0);
        summary.setGoalsCompletedToday(0);
        summary.setGoalsCompletedThisWeek(0);
        summary.setGoalsCompletedThisMonth(0);
        return summary;
    }

    private EvolutionTrackingEntity createMockTracking(EvolutionTrackingEntity.ActionType actionType, int xpEarned) {
        EvolutionTrackingEntity tracking = new EvolutionTrackingEntity();
        tracking.setId(UUID.randomUUID());
        tracking.setUsuarioId(USER_ID);
        tracking.setActionType(actionType);
        tracking.setXpEarned(xpEarned);
        tracking.setXpTotal(xpEarned);
        tracking.setLevel(1);
        tracking.setDescription("Test action");
        tracking.setDataRegistro(LocalDateTime.now());
        return tracking;
    }
}
