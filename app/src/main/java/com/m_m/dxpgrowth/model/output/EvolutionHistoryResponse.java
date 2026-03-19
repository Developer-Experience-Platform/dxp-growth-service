package com.m_m.dxpgrowth.model.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvolutionHistoryResponse {
    private UUID id;
    private String actionType;
    private Integer xpEarned;
    private Integer xpTotal;
    private Integer level;
    private String description;
    private UUID goalReferenceId;
    private UUID goalTypeReferenceId;
    private LocalDateTime dataRegistro;
}
