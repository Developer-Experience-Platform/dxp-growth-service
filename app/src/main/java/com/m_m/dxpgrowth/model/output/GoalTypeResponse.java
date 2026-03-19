package com.m_m.dxpgrowth.model.output;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record GoalTypeResponse(
        UUID id,
        String nome,
        String descricao,
        String cor,
        LocalDateTime dataHoraCriacao
) {}
