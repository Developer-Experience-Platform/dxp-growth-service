package com.m_m.dxpgrowth.controller;

import com.m_m.dxpgrowth.model.output.EvolutionHistoryResponse;
import com.m_m.dxpgrowth.model.output.EvolutionSummaryResponse;
import com.m_m.dxpgrowth.model.output.GoalTypeMetricResponse;
import com.m_m.dxpgrowth.model.output.UserEvolutionResponse;
import com.m_m.dxpgrowth.service.EvolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evolution")
@RequiredArgsConstructor
public class EvolutionController {

    private final EvolutionService evolutionService;

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<UserEvolutionResponse> getFullEvolution(@PathVariable UUID userId) {
        var response = evolutionService.getFullEvolution(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{userId}/summary")
    public ResponseEntity<EvolutionSummaryResponse> getSummary(@PathVariable UUID userId) {
        var response = evolutionService.getEvolutionSummary(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{userId}/history")
    public ResponseEntity<List<EvolutionHistoryResponse>> getHistory(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "20") int limit) {
        var response = evolutionService.getEvolutionHistory(userId, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{userId}/metrics")
    public ResponseEntity<List<GoalTypeMetricResponse>> getMetrics(@PathVariable UUID userId) {
        var response = evolutionService.getGoalTypeMetrics(userId);
        return ResponseEntity.ok(response);
    }
}
