package com.m_m.dxpgrowth.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "goal_type_db")
public class GoalTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "goal_type_id", length = 36, nullable = false, unique = true)
    private UUID id;

    @Column(name = "nome_tipo", length = 255, nullable = false)
    private String nome;

    @Column(name = "descricao_tipo", length = 500)
    private String descricao;

    @Column(name = "cor_tipo", length = 7)
    private String cor;

    @Column(name = "data_criacao_tipo", nullable = false)
    private LocalDateTime dataHoraCriacao;

    @PrePersist
    public void onCreate() {
        this.dataHoraCriacao = LocalDateTime.now();
    }
}
