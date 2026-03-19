package com.m_m.dxpgrowth.service;

import com.m_m.dxpgrowth.mapper.GoalMapper;
import com.m_m.dxpgrowth.mapper.GoalTypeMapper;
import com.m_m.dxpgrowth.model.input.GoalInput;
import com.m_m.dxpgrowth.model.input.GoalTypeInput;
import com.m_m.dxpgrowth.model.output.GoalResponse;
import com.m_m.dxpgrowth.model.output.GoalTypeResponse;
import com.m_m.dxpgrowth.persistence.entity.*;
import com.m_m.dxpgrowth.persistence.repository.GoalRepository;
import com.m_m.dxpgrowth.persistence.repository.GoalTypeRepository;
import com.m_m.dxpgrowth.service.impl.GrowthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrowthServiceImplTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalTypeRepository goalTypeRepository;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private GoalTypeMapper goalTypeMapper;

    @Mock
    private EvolutionService evolutionService;

    @InjectMocks
    private GrowthServiceImpl growthService;

    private final UUID USER_ID = UUID.randomUUID();
    private final UUID GOAL_ID = UUID.randomUUID();
    private final UUID GOAL_TYPE_ID = UUID.randomUUID();

    private GoalEntity mockGoalEntity;
    private GoalTypeEntity mockGoalTypeEntity;
    private GoalInput mockGoalInput;
    private GoalTypeInput mockGoalTypeInput;
    private GoalResponse mockGoalResponse;
    private GoalTypeResponse mockGoalTypeResponse;

    @BeforeEach
    void setUp() {
        mockGoalTypeEntity = new GoalTypeEntity();
        mockGoalTypeEntity.setId(GOAL_TYPE_ID);
        mockGoalTypeEntity.setNome("Programação");
        mockGoalTypeEntity.setDescricao("Metas de programação");
        mockGoalTypeEntity.setCor("#FF5733");

        mockGoalEntity = new GoalEntity();
        mockGoalEntity.setId(GOAL_ID);
        mockGoalEntity.setUsuarioId(USER_ID);
        mockGoalEntity.setTitulo("Aprender Spring Boot");
        mockGoalEntity.setDescricao("Estudar Spring Boot");
        mockGoalEntity.setTipo(mockGoalTypeEntity);
        mockGoalEntity.setStatus(StatusGoal.NAO_INICIADO);
        mockGoalEntity.setPrazo(LocalDate.now().plusMonths(1));

        mockGoalInput = new GoalInput();
        mockGoalInput.setUsuarioId(USER_ID);
        mockGoalInput.setTitulo("Aprender Spring Boot");
        mockGoalInput.setGoalTypeId(GOAL_TYPE_ID);
        mockGoalInput.setStatus(StatusGoal.NAO_INICIADO);

        mockGoalTypeInput = new GoalTypeInput();
        mockGoalTypeInput.setNome("Programação");
        mockGoalTypeInput.setDescricao("Metas de programação");
        mockGoalTypeInput.setCor("#FF5733");

        mockGoalTypeResponse = GoalTypeResponse.builder()
                .id(GOAL_TYPE_ID)
                .nome("Programação")
                .descricao("Metas de programação")
                .cor("#FF5733")
                .build();

        mockGoalResponse = GoalResponse.builder()
                .id(GOAL_ID)
                .usuarioId(USER_ID)
                .titulo("Aprender Spring Boot")
                .status(StatusGoal.NAO_INICIADO)
                .build();
    }

    @Test
    void createGoalType_ShouldReturnCreatedGoalType() {
        when(goalTypeMapper.toEntity(any(GoalTypeInput.class))).thenReturn(mockGoalTypeEntity);
        when(goalTypeRepository.save(any(GoalTypeEntity.class))).thenReturn(mockGoalTypeEntity);
        when(goalTypeMapper.toResponse(any(GoalTypeEntity.class))).thenReturn(mockGoalTypeResponse);

        GoalTypeResponse result = growthService.createGoalType(mockGoalTypeInput);

        assertNotNull(result);
        assertEquals("Programação", result.nome());
        verify(goalTypeRepository, times(1)).save(any(GoalTypeEntity.class));
        verify(evolutionService, times(1)).registerAction(any(), any(), any(), any(), any());
    }

    @Test
    void getAllGoalTypes_ShouldReturnAllGoalTypes() {
        when(goalTypeRepository.findAll()).thenReturn(List.of(mockGoalTypeEntity));
        when(goalTypeMapper.toResponseList(anyList())).thenReturn(List.of(mockGoalTypeResponse));

        List<GoalTypeResponse> result = growthService.getAllGoalTypes();

        assertEquals(1, result.size());
        assertEquals("Programação", result.get(0).nome());
    }

    @Test
    void getGoalTypeById_WhenExists_ShouldReturnGoalType() {
        when(goalTypeRepository.findById(GOAL_TYPE_ID)).thenReturn(Optional.of(mockGoalTypeEntity));
        when(goalTypeMapper.toResponse(any(GoalTypeEntity.class))).thenReturn(mockGoalTypeResponse);

        GoalTypeResponse result = growthService.getGoalTypeById(GOAL_TYPE_ID);

        assertNotNull(result);
        assertEquals(GOAL_TYPE_ID, result.id());
    }

    @Test
    void getGoalTypeById_WhenNotExists_ShouldThrowException() {
        when(goalTypeRepository.findById(GOAL_TYPE_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> growthService.getGoalTypeById(GOAL_TYPE_ID));
    }

    @Test
    void createGoal_ShouldReturnCreatedGoalAndRegisterEvolution() {
        when(goalTypeRepository.findById(GOAL_TYPE_ID)).thenReturn(Optional.of(mockGoalTypeEntity));
        when(goalMapper.toEntity(any(GoalInput.class))).thenReturn(mockGoalEntity);
        when(goalRepository.save(any(GoalEntity.class))).thenReturn(mockGoalEntity);
        when(goalMapper.toResponse(any(GoalEntity.class))).thenReturn(mockGoalResponse);

        GoalResponse result = growthService.createGoal(mockGoalInput);

        assertNotNull(result);
        assertEquals("Aprender Spring Boot", result.titulo());
        verify(goalRepository, times(1)).save(any(GoalEntity.class));
        verify(evolutionService, times(1)).registerAction(
                eq(USER_ID),
                eq(EvolutionTrackingEntity.ActionType.GOAL_CREATED),
                any(),
                eq(GOAL_TYPE_ID),
                anyString()
        );
    }

    @Test
    void createGoal_WhenGoalTypeNotFound_ShouldThrowException() {
        when(goalTypeRepository.findById(GOAL_TYPE_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> growthService.createGoal(mockGoalInput));
    }

    @Test
    void getAllGoals_ShouldReturnAllGoals() {
        when(goalRepository.findAll()).thenReturn(List.of(mockGoalEntity));
        when(goalMapper.toResponseList(anyList())).thenReturn(List.of(mockGoalResponse));

        List<GoalResponse> result = growthService.getAllGoals();

        assertEquals(1, result.size());
        assertEquals("Aprender Spring Boot", result.get(0).titulo());
    }

    @Test
    void getGoalsByUser_ShouldReturnUserGoals() {
        when(goalRepository.findByUsuarioId(USER_ID)).thenReturn(List.of(mockGoalEntity));
        when(goalMapper.toResponseList(anyList())).thenReturn(List.of(mockGoalResponse));

        List<GoalResponse> result = growthService.getGoalsByUser(USER_ID);

        assertEquals(1, result.size());
        assertEquals(USER_ID, result.get(0).usuarioId());
    }

    @Test
    void getGoalsByUserAndStatus_ShouldReturnFilteredGoals() {
        when(goalRepository.findByUsuarioIdAndStatus(USER_ID, StatusGoal.NAO_INICIADO))
                .thenReturn(List.of(mockGoalEntity));
        when(goalMapper.toResponseList(anyList())).thenReturn(List.of(mockGoalResponse));

        List<GoalResponse> result = 
                growthService.getGoalsByUserAndStatus(USER_ID, StatusGoal.NAO_INICIADO);

        assertEquals(1, result.size());
    }

    @Test
    void updateGoal_WhenStatusChanges_ShouldRegisterEvolution() {
        GoalInput updateInput = new GoalInput();
        updateInput.setUsuarioId(USER_ID);
        updateInput.setTitulo("Aprender Spring Boot");
        updateInput.setGoalTypeId(GOAL_TYPE_ID);
        updateInput.setStatus(StatusGoal.CONCLUIDO);

        GoalEntity existingGoal = new GoalEntity();
        existingGoal.setId(GOAL_ID);
        existingGoal.setUsuarioId(USER_ID);
        existingGoal.setStatus(StatusGoal.EM_ANDAMENTO);
        existingGoal.setTipo(mockGoalTypeEntity);

        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(existingGoal));
        when(goalTypeRepository.findById(GOAL_TYPE_ID)).thenReturn(Optional.of(mockGoalTypeEntity));
        when(goalMapper.updateEntityFromInput(any(), any())).thenReturn(existingGoal);
        when(goalRepository.save(any(GoalEntity.class))).thenReturn(existingGoal);
        when(goalMapper.toResponse(any(GoalEntity.class))).thenReturn(mockGoalResponse);
        when(evolutionService.getActionTypeForGoalStatus(StatusGoal.CONCLUIDO))
                .thenReturn(EvolutionTrackingEntity.ActionType.GOAL_COMPLETED);

        GoalResponse result = growthService.updateGoal(updateInput, GOAL_ID);

        assertNotNull(result);
        verify(evolutionService, times(1)).registerAction(
                eq(USER_ID),
                eq(EvolutionTrackingEntity.ActionType.GOAL_COMPLETED),
                any(),
                any(),
                anyString()
        );
    }

    @Test
    void updateGoal_WhenStatusUnchanged_ShouldNotRegisterEvolution() {
        GoalInput updateInput = new GoalInput();
        updateInput.setUsuarioId(USER_ID);
        updateInput.setTitulo("Aprender Spring Boot");
        updateInput.setGoalTypeId(GOAL_TYPE_ID);
        updateInput.setStatus(StatusGoal.EM_ANDAMENTO);

        GoalEntity existingGoal = new GoalEntity();
        existingGoal.setId(GOAL_ID);
        existingGoal.setUsuarioId(USER_ID);
        existingGoal.setStatus(StatusGoal.EM_ANDAMENTO);
        existingGoal.setTipo(mockGoalTypeEntity);

        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(existingGoal));
        when(goalTypeRepository.findById(GOAL_TYPE_ID)).thenReturn(Optional.of(mockGoalTypeEntity));
        when(goalMapper.updateEntityFromInput(any(), any())).thenReturn(existingGoal);
        when(goalRepository.save(any(GoalEntity.class))).thenReturn(existingGoal);
        when(goalMapper.toResponse(any(GoalEntity.class))).thenReturn(mockGoalResponse);

        GoalResponse result = growthService.updateGoal(updateInput, GOAL_ID);

        assertNotNull(result);
        verify(evolutionService, never()).registerAction(any(), any(), any(), any(), any());
    }

    @Test
    void deleteGoal_ShouldDeleteAndRegisterEvolution() {
        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(mockGoalEntity));

        growthService.deleteGoal(GOAL_ID);

        verify(goalRepository, times(1)).deleteById(GOAL_ID);
        verify(evolutionService, times(1)).registerAction(
                eq(USER_ID),
                eq(EvolutionTrackingEntity.ActionType.GOAL_CANCELLED),
                eq(GOAL_ID),
                eq(GOAL_TYPE_ID),
                anyString()
        );
    }
}
