package com.m_m.dxpgrowth.persistence.repository;

import com.m_m.dxpgrowth.persistence.entity.GoalTypeMetricEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalTypeMetricRepository extends JpaRepository<GoalTypeMetricEntity, UUID> {

    Optional<GoalTypeMetricEntity> findByUsuarioIdAndGoalTypeId(UUID usuarioId, UUID goalTypeId);

    List<GoalTypeMetricEntity> findByUsuarioId(UUID usuarioId);

    List<GoalTypeMetricEntity> findByUsuarioIdOrderByCompletionRateDesc(UUID usuarioId);
}
