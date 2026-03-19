package com.m_m.dxpgrowth.persistence.repository;

import com.m_m.dxpgrowth.persistence.entity.EvolutionTrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EvolutionTrackingRepository extends JpaRepository<EvolutionTrackingEntity, UUID> {

    List<EvolutionTrackingEntity> findByUsuarioIdOrderByDataRegistroDesc(UUID usuarioId);

    List<EvolutionTrackingEntity> findByUsuarioIdAndActionTypeOrderByDataRegistroDesc(
            UUID usuarioId, EvolutionTrackingEntity.ActionType actionType);

    @Query("SELECT e FROM EvolutionTrackingEntity e WHERE e.usuarioId = :usuarioId AND e.dataRegistro >= :since ORDER BY e.dataRegistro DESC")
    List<EvolutionTrackingEntity> findByUsuarioIdSince(UUID usuarioId, LocalDateTime since);

    @Query("SELECT SUM(e.xpEarned) FROM EvolutionTrackingEntity e WHERE e.usuarioId = :usuarioId")
    Integer sumXpByUsuarioId(UUID usuarioId);

    @Query("SELECT COUNT(e) FROM EvolutionTrackingEntity e WHERE e.usuarioId = :usuarioId AND e.actionType = :actionType")
    Long countByUsuarioIdAndActionType(UUID usuarioId, EvolutionTrackingEntity.ActionType actionType);
}
