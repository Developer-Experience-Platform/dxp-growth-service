package com.m_m.dxpgrowth.model.output;

import java.time.LocalDateTime;
import java.util.UUID;

public record GoalTypeResponse(
        UUID id,
        String nome,
        String descricao,
        String cor,
        LocalDateTime dataHoraCriacao
) {}
