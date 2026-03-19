package com.m_m.dxpgrowth.persistence.repository;

import com.m_m.dxpgrowth.persistence.entity.GoalTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalTypeRepository extends JpaRepository<GoalTypeEntity, UUID> {

    Optional<GoalTypeEntity> findByNome(String nome);
}
