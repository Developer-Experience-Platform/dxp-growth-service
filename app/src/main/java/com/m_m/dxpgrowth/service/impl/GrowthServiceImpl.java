package com.m_m.dxpgrowth.service.impl;

import com.m_m.dxpgrowth.mapper.GoalMapper;
import com.m_m.dxpgrowth.mapper.GoalTypeMapper;
import com.m_m.dxpgrowth.model.input.GoalInput;
import com.m_m.dxpgrowth.model.input.GoalTypeInput;
import com.m_m.dxpgrowth.model.output.GoalResponse;
import com.m_m.dxpgrowth.model.output.GoalTypeResponse;
import com.m_m.dxpgrowth.persistence.entity.EvolutionTrackingEntity;
import com.m_m.dxpgrowth.persistence.entity.StatusGoal;
import com.m_m.dxpgrowth.persistence.repository.GoalRepository;
import com.m_m.dxpgrowth.persistence.repository.GoalTypeRepository;
import com.m_m.dxpgrowth.service.EvolutionService;
import com.m_m.dxpgrowth.service.GrowthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class GrowthServiceImpl implements GrowthService {

    private final GoalRepository goalRepository;
    private final GoalTypeRepository goalTypeRepository;
    private final GoalMapper goalMapper;
    private final GoalTypeMapper goalTypeMapper;
    private final EvolutionService evolutionService;

    @Override
    public GoalTypeResponse createGoalType(GoalTypeInput input) {
        var entity = goalTypeMapper.toEntity(input);
        var saved = goalTypeRepository.save(entity);

        evolutionService.registerAction(
                UUID.randomUUID(),
                EvolutionTrackingEntity.ActionType.GOAL_TYPE_CREATED,
                null,
                saved.getId(),
                "Tipo de meta criado: " + saved.getNome()
        );

        return goalTypeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalTypeResponse> getAllGoalTypes() {
        var entities = goalTypeRepository.findAll();
        return goalTypeMapper.toResponseList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public GoalTypeResponse getGoalTypeById(UUID id) {
        var entity = goalTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de meta não encontrado com id: " + id));
        return goalTypeMapper.toResponse(entity);
    }

    @Override
    public GoalTypeResponse updateGoalType(GoalTypeInput input, UUID id) {
        var existingEntity = goalTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de meta não encontrado com id: " + id));
        var updatedEntity = goalTypeMapper.updateEntityFromInput(input, existingEntity);
        var saved = goalTypeRepository.save(updatedEntity);
        return goalTypeMapper.toResponse(saved);
    }

    @Override
    public void deleteGoalType(UUID id) {
        if (!goalTypeRepository.existsById(id)) {
            throw new RuntimeException("Tipo de meta não encontrado com id: " + id);
        }
        goalTypeRepository.deleteById(id);
    }

    @Override
    public GoalResponse createGoal(GoalInput input) {
        var tipo = goalTypeRepository.findById(input.getGoalTypeId())
                .orElseThrow(() -> new RuntimeException("Tipo de meta não encontrado com id: " + input.getGoalTypeId()));

        var entity = goalMapper.toEntity(input);
        entity.setTipo(tipo);
        var saved = goalRepository.save(entity);

        evolutionService.registerAction(
                input.getUsuarioId(),
                EvolutionTrackingEntity.ActionType.GOAL_CREATED,
                saved.getId(),
                tipo.getId(),
                "Meta criada: " + saved.getTitulo()
        );

        return goalMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalResponse> getAllGoals() {
        var entities = goalRepository.findAll();
        return goalMapper.toResponseList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public GoalResponse getGoalById(UUID id) {
        var entity = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada com id: " + id));
        return goalMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalResponse> getGoalsByUser(UUID userId) {
        var entities = goalRepository.findByUsuarioId(userId);
        return goalMapper.toResponseList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalResponse> getGoalsByUserAndStatus(UUID userId, StatusGoal status) {
        var entities = goalRepository.findByUsuarioIdAndStatus(userId, status);
        return goalMapper.toResponseList(entities);
    }

    @Override
    public GoalResponse updateGoal(GoalInput input, UUID id) {
        var existingEntity = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada com id: " + id));

        var tipo = goalTypeRepository.findById(input.getGoalTypeId())
                .orElseThrow(() -> new RuntimeException("Tipo de meta não encontrado com id: " + input.getGoalTypeId()));

        StatusGoal previousStatus = existingEntity.getStatus();
        StatusGoal newStatus = input.getStatus();

        var updatedEntity = goalMapper.updateEntityFromInput(input, existingEntity);
        updatedEntity.setTipo(tipo);
        var saved = goalRepository.save(updatedEntity);

        if (previousStatus != newStatus) {
            var actionType = evolutionService.getActionTypeForGoalStatus(newStatus);
            evolutionService.registerAction(
                    input.getUsuarioId(),
                    actionType,
                    saved.getId(),
                    tipo.getId(),
                    "Meta atualizada para " + newStatus.name() + ": " + saved.getTitulo()
            );
        }

        return goalMapper.toResponse(saved);
    }

    @Override
    public void deleteGoal(UUID id) {
        var existingEntity = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada com id: " + id));

        evolutionService.registerAction(
                existingEntity.getUsuarioId(),
                EvolutionTrackingEntity.ActionType.GOAL_CANCELLED,
                id,
                existingEntity.getTipo() != null ? existingEntity.getTipo().getId() : null,
                "Meta removida: " + existingEntity.getTitulo()
        );

        goalRepository.deleteById(id);
    }
}
