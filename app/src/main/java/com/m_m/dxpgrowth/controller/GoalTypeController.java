package com.m_m.dxpgrowth.controller;

import com.m_m.dxpgrowth.model.input.GoalTypeInput;
import com.m_m.dxpgrowth.model.output.GoalTypeResponse;
import com.m_m.dxpgrowth.service.GrowthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/goal-types")
@RequiredArgsConstructor
public class GoalTypeController {

    private final GrowthService growthService;

    @PostMapping
    public ResponseEntity<GoalTypeResponse> createGoalType(@RequestBody @Valid GoalTypeInput request) {
        var response = growthService.createGoalType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GoalTypeResponse>> getAllGoalTypes() {
        var response = growthService.getAllGoalTypes();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalTypeResponse> getGoalTypeById(@PathVariable UUID id) {
        var response = growthService.getGoalTypeById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalTypeResponse> updateGoalType(@RequestBody @Valid GoalTypeInput request, @PathVariable UUID id) {
        var response = growthService.updateGoalType(request, id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoalType(@PathVariable UUID id) {
        growthService.deleteGoalType(id);
        return ResponseEntity.noContent().build();
    }
}
