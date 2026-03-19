package com.m_m.dxpgrowth.persistence.repository;

import com.m_m.dxpgrowth.persistence.entity.UserEvolutionSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEvolutionSummaryRepository extends JpaRepository<UserEvolutionSummaryEntity, UUID> {

    Optional<UserEvolutionSummaryEntity> findByUsuarioId(UUID usuarioId);
}
