package com.m_m.dxpgrowth.controller;

import com.m_m.dxpgrowth.model.input.GoalInput;
import com.m_m.dxpgrowth.model.output.GoalResponse;
import com.m_m.dxpgrowth.persistence.entity.StatusGoal;
import com.m_m.dxpgrowth.service.GrowthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GrowthService growthService;

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@RequestBody @Valid GoalInput request) {
        var response = growthService.createGoal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> getAllGoals() {
        var response = growthService.getAllGoals();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoalById(@PathVariable UUID id) {
        var response = growthService.getGoalById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<GoalResponse>> getGoalsByUser(@PathVariable UUID userId) {
        var response = growthService.getGoalsByUser(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{userId}/status/{status}")
    public ResponseEntity<List<GoalResponse>> getGoalsByUserAndStatus(
            @PathVariable UUID userId,
            @PathVariable StatusGoal status) {
        var response = growthService.getGoalsByUserAndStatus(userId, status);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(@RequestBody @Valid GoalInput request, @PathVariable UUID id) {
        var response = growthService.updateGoal(request, id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable UUID id) {
        growthService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }
}
