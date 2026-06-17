package com.gymbackend.infrastructure.persistence;

import com.gymbackend.domain.entities.Exercicio;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA para Exercício.
 * Mapeamento entre domínio e banco de dados.
 */
@Entity
@Table(name = "exercicios")
class ExercicioJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "descricao", nullable = false, length = 1000)
    private String descricao;

    @Column(name = "grupo_muscular", nullable = false, length = 100)
    private String grupoMuscular;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Exercicio.StatusExercicio status;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @Column(name = "deletado_em")
    private LocalDateTime deletadoEm;

    @Version
    @Column(name = "versao", nullable = false)
    private Long versao;

    public ExercicioJpa() {}

    public static ExercicioJpa fromDomain(Exercicio exercicio) {
        ExercicioJpa jpa = new ExercicioJpa();
        jpa.setId(exercicio.getId());
        jpa.setNome(exercicio.getNome());
        jpa.setDescricao(exercicio.getDescricao());
        jpa.setGrupoMuscular(exercicio.getGrupoMuscular());
        jpa.setStatus(exercicio.getStatus());
        jpa.setCriadoEm(exercicio.getCriadoEm());
        jpa.setAtualizadoEm(exercicio.getAtualizadoEm());
        jpa.setDeletadoEm(exercicio.getDeletadoEm());
        jpa.setVersao(exercicio.getVersao() != null ? exercicio.getVersao() : 0L);
        return jpa;
    }

    public Exercicio toDomain() {
        Exercicio exercicio = new Exercicio();
        exercicio.setId(id);
        exercicio.setNome(nome);
        exercicio.setDescricao(descricao);
        exercicio.setGrupoMuscular(grupoMuscular);
        exercicio.setStatus(status);
        exercicio.setCriadoEm(criadoEm);
        exercicio.setAtualizadoEm(atualizadoEm);
        exercicio.setDeletadoEm(deletadoEm);
        exercicio.setVersao(versao);
        return exercicio;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }
    public Exercicio.StatusExercicio getStatus() { return status; }
    public void setStatus(Exercicio.StatusExercicio status) { this.status = status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
    public LocalDateTime getDeletadoEm() { return deletadoEm; }
    public void setDeletadoEm(LocalDateTime deletadoEm) { this.deletadoEm = deletadoEm; }
    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }
}

/**
 * Implementação do repositório de Exercícios usando JPA/Panache.
 */
@ApplicationScoped
class ExercicioJpaRepository implements PanacheRepositoryBase<ExercicioJpa, UUID> {}
