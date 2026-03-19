package com.m_m.dxpgrowth.model.input;

import com.m_m.dxpgrowth.persistence.entity.StatusGoal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class GoalInput {

    @NotNull(message = "ID do usuário é obrigatório")
    private UUID usuarioId;

    @NotBlank(message = "Título da meta é obrigatório")
    private String titulo;

    private String descricao;

    @NotNull(message = "Tipo da meta é obrigatório")
    private UUID goalTypeId;

    @NotNull(message = "Status é obrigatório")
    private StatusGoal status;

    private LocalDate prazo;
}
