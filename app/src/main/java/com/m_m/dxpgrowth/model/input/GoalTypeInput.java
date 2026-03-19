package com.m_m.dxpgrowth.model.input;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class GoalTypeInput {

    @NotBlank(message = "Nome do tipo é obrigatório")
    private String nome;

    private String descricao;
    private String cor;
}
