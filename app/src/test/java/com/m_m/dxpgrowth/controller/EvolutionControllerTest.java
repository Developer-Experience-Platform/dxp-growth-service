package com.m_m.dxpgrowth.controller;

import com.m_m.dxpgrowth.controller.EvolutionController;
import com.m_m.dxpgrowth.model.output.EvolutionHistoryResponse;
import com.m_m.dxpgrowth.model.output.EvolutionSummaryResponse;
import com.m_m.dxpgrowth.model.output.GoalTypeMetricResponse;
import com.m_m.dxpgrowth.model.output.UserEvolutionResponse;
import com.m_m.dxpgrowth.service.EvolutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvolutionControllerTest {

    @Mock
    private EvolutionService evolutionService;

    @InjectMocks
    private EvolutionController evolutionController;

    private final UUID USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
    }

    @Test
    void getFullEvolution_ShouldReturnCompleteEvolution() {
        UserEvolutionResponse mockResponse = createMockEvolutionResponse();
        when(evolutionService.getFullEvolution(USER_ID)).thenReturn(mockResponse);

        ResponseEntity<UserEvolutionResponse> response = evolutionController.getFullEvolution(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(USER_ID, response.getBody().getUsuarioId());
        assertEquals(5, response.getBody().getSummary().getLevel());
        assertEquals(250, response.getBody().getSummary().getXpTotal());

        verify(evolutionService, times(1)).getFullEvolution(USER_ID);
    }

    @Test
    void getSummary_ShouldReturnEvolutionSummary() {
        EvolutionSummaryResponse mockSummary = createMockSummaryResponse();
        when(evolutionService.getEvolutionSummary(USER_ID)).thenReturn(mockSummary);

        ResponseEntity<EvolutionSummaryResponse> response = evolutionController.getSummary(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().getLevel());
        assertEquals(250, response.getBody().getXpTotal());
        assertEquals(7, response.getBody().getCurrentStreak());

        verify(evolutionService, times(1)).getEvolutionSummary(USER_ID);
    }

    @Test
    void getHistory_WithDefaultLimit_ShouldReturnHistory() {
        List<EvolutionHistoryResponse> mockHistory = List.of(
                createMockHistory("GOAL_COMPLETED", 50),
                createMockHistory("GOAL_CREATED", 10)
        );
        when(evolutionService.getEvolutionHistory(USER_ID, 20)).thenReturn(mockHistory);

        ResponseEntity<List<EvolutionHistoryResponse>> response = 
                evolutionController.getHistory(USER_ID, 20);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(evolutionService, times(1)).getEvolutionHistory(USER_ID, 20);
    }

    @Test
    void getHistory_WithCustomLimit_ShouldReturnLimitedHistory() {
        when(evolutionService.getEvolutionHistory(USER_ID, 5)).thenReturn(List.of());

        ResponseEntity<List<EvolutionHistoryResponse>> response = 
                evolutionController.getHistory(USER_ID, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(evolutionService, times(1)).getEvolutionHistory(USER_ID, 5);
    }

    @Test
    void getMetrics_ShouldReturnGoalTypeMetrics() {
        List<GoalTypeMetricResponse> mockMetrics = List.of(
                createMockMetric("Programação", 70.0),
                createMockMetric("Idiomas", 85.0)
        );
        when(evolutionService.getGoalTypeMetrics(USER_ID)).thenReturn(mockMetrics);

        ResponseEntity<List<GoalTypeMetricResponse>> response = 
                evolutionController.getMetrics(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Programação", response.getBody().get(0).getGoalTypeName());

        verify(evolutionService, times(1)).getGoalTypeMetrics(USER_ID);
    }

    @Test
    void getMetrics_WithEmptyMetrics_ShouldReturnEmptyList() {
        when(evolutionService.getGoalTypeMetrics(USER_ID)).thenReturn(List.of());

        ResponseEntity<List<GoalTypeMetricResponse>> response = 
                evolutionController.getMetrics(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    private UserEvolutionResponse createMockEvolutionResponse() {
        return UserEvolutionResponse.builder()
                .usuarioId(USER_ID)
                .summary(createMockSummaryResponse())
                .levelInfo(UserEvolutionResponse.LevelInfo.builder()
                        .currentLevel(5)
                        .xpCurrent(50)
                        .xpForNextLevel(300)
                        .xpRemaining(250)
                        .progressPercent(45.5)
                        .build())
                .recentHistory(List.of())
                .goalTypeMetrics(List.of())
                .build();
    }

    private EvolutionSummaryResponse createMockSummaryResponse() {
        return EvolutionSummaryResponse.builder()
                .usuarioId(USER_ID)
                .xpTotal(250)
                .level(5)
                .progressToNextLevel(45.5)
                .xpForCurrentLevel(100)
                .xpForNextLevel(300)
                .totalGoalsCreated(10)
                .totalGoalsCompleted(5)
                .totalGoalsCancelled(1)
                .currentStreak(7)
                .longestStreak(14)
                .lastActivityDate(LocalDate.now())
                .goalsCompletedToday(1)
                .goalsCompletedThisWeek(3)
                .goalsCompletedThisMonth(8)
                .build();
    }

    private EvolutionHistoryResponse createMockHistory(String actionType, int xpEarned) {
        return EvolutionHistoryResponse.builder()
                .id(UUID.randomUUID())
                .actionType(actionType)
                .xpEarned(xpEarned)
                .xpTotal(250)
                .level(5)
                .description("Test action")
                .build();
    }

    private GoalTypeMetricResponse createMockMetric(String goalTypeName, Double completionRate) {
        return GoalTypeMetricResponse.builder()
                .id(UUID.randomUUID())
                .goalTypeId(UUID.randomUUID())
                .goalTypeName(goalTypeName)
                .totalGoals(10)
                .completedGoals(7)
                .completionRate(completionRate)
                .build();
    }
}
