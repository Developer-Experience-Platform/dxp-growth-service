package com.m_m.dxpgrowth.persistence.repository;

import com.m_m.dxpgrowth.persistence.entity.GoalEntity;
import com.m_m.dxpgrowth.persistence.entity.StatusGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoalRepository extends JpaRepository<GoalEntity, UUID> {

    List<GoalEntity> findByUsuarioId(UUID usuarioId);

    List<GoalEntity> findByUsuarioIdAndStatus(UUID usuarioId, StatusGoal status);

    List<GoalEntity> findByUsuarioIdAndTipoId(UUID usuarioId, UUID goalTypeId);
}
