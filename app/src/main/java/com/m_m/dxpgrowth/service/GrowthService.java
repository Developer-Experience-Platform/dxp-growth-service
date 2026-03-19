package com.m_m.dxpgrowth.service;

import com.m_m.dxpgrowth.model.input.GoalInput;
import com.m_m.dxpgrowth.model.input.GoalTypeInput;
import com.m_m.dxpgrowth.model.output.GoalResponse;
import com.m_m.dxpgrowth.model.output.GoalTypeResponse;
import com.m_m.dxpgrowth.persistence.entity.StatusGoal;

import java.util.List;
import java.util.UUID;

public interface GrowthService {

    // GoalType CRUD
    GoalTypeResponse createGoalType(GoalTypeInput input);
    List<GoalTypeResponse> getAllGoalTypes();
    GoalTypeResponse getGoalTypeById(UUID id);
    GoalTypeResponse updateGoalType(GoalTypeInput input, UUID id);
    void deleteGoalType(UUID id);

    // Goal CRUD
    GoalResponse createGoal(GoalInput input);
    List<GoalResponse> getAllGoals();
    GoalResponse getGoalById(UUID id);
    List<GoalResponse> getGoalsByUser(UUID userId);
    List<GoalResponse> getGoalsByUserAndStatus(UUID userId, StatusGoal status);
    GoalResponse updateGoal(GoalInput input, UUID id);
    void deleteGoal(UUID id);
}
