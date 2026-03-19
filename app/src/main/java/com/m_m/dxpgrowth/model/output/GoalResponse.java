package com.m_m.dxpgrowth.model.output;

import com.m_m.dxpgrowth.persistence.entity.StatusGoal;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record GoalResponse(
        UUID id,
        UUID usuarioId,
        String titulo,
        String descricao,
        GoalTypeResponse tipo,
        StatusGoal status,
        LocalDate prazo,
        LocalDateTime dataHoraCriacao,
        LocalDateTime dataHoraAtualizacao
) {}
