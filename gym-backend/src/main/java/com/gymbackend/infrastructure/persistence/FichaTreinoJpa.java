package com.gymbackend.infrastructure.persistence;

import com.gymbackend.domain.entities.FichaTreino;
import com.gymbackend.domain.repositories.FichaTreinoRepository;
import com.gymbackend.domain.entities.VersaoFichaTreino;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Entidade JPA para Ficha de Treino.
 */
@Entity
@Table(name = "fichas_treino")
class FichaTreinoJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "usuario_id", nullable = false, columnDefinition = "UUID")
    private UUID usuarioId;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "versao_ativa_id", columnDefinition = "UUID")
    private UUID versaoAtivaId;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @Version
    @Column(name = "versao", nullable = false)
    private Long versao;

    public FichaTreinoJpa() {}

    public static FichaTreinoJpa fromDomain(FichaTreino ficha) {
        FichaTreinoJpa jpa = new FichaTreinoJpa();
        jpa.setId(ficha.getId());
        jpa.setUsuarioId(ficha.getUsuarioId());
        jpa.setNome(ficha.getNome());
        jpa.setVersaoAtivaId(ficha.getVersaoAtivaId());
        jpa.setCriadoEm(ficha.getCriadoEm());
        jpa.setAtualizadoEm(ficha.getAtualizadoEm());
        jpa.setVersao(ficha.getVersao() != null ? ficha.getVersao() : 0L);
        return jpa;
    }

    public FichaTreino toDomain() {
        FichaTreino ficha = new FichaTreino();
        ficha.setId(id);
        ficha.setUsuarioId(usuarioId);
        ficha.setNome(nome);
        ficha.setVersaoAtivaId(versaoAtivaId);
        ficha.setCriadoEm(criadoEm);
        ficha.setAtualizadoEm(atualizadoEm);
        ficha.setVersao(versao);
        return ficha;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public UUID getVersaoAtivaId() { return versaoAtivaId; }
    public void setVersaoAtivaId(UUID versaoAtivaId) { this.versaoAtivaId = versaoAtivaId; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }
}

@ApplicationScoped
class FichaTreinoJpaRepository implements PanacheRepositoryBase<FichaTreinoJpa, UUID> {}

/**
 * Entidade JPA para Versão de Ficha de Treino.
 */
@Entity
@Table(name = "versoes_ficha_treino")
class VersaoFichaTreinoJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "ficha_treino_id", nullable = false, columnDefinition = "UUID")
    private UUID fichaTreinoId;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "deletado_em")
    private LocalDateTime deletadoEm;

    @Version
    @Column(name = "versao", nullable = false)
    private Long versao;

    public VersaoFichaTreinoJpa() {}

    public static VersaoFichaTreinoJpa fromDomain(VersaoFichaTreino v) {
        VersaoFichaTreinoJpa jpa = new VersaoFichaTreinoJpa();
        jpa.setId(v.getId());
        jpa.setFichaTreinoId(v.getFichaTreinoId());
        jpa.setNumero(v.getNumero());
        jpa.setCriadoEm(v.getCriadoEm());
        jpa.setDeletadoEm(v.getDeletadoEm());
        jpa.setVersao(v.getVersao() != null ? v.getVersao() : 0L);
        return jpa;
    }

    public VersaoFichaTreino toDomain() {
        VersaoFichaTreino v = new VersaoFichaTreino();
        v.setId(id);
        v.setFichaTreinoId(fichaTreinoId);
        v.setNumero(numero);
        v.setCriadoEm(criadoEm);
        v.setDeletadoEm(deletadoEm);
        v.setVersao(versao);
        return v;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getFichaTreinoId() { return fichaTreinoId; }
    public void setFichaTreinoId(UUID fichaTreinoId) { this.fichaTreinoId = fichaTreinoId; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getDeletadoEm() { return deletadoEm; }
    public void setDeletadoEm(LocalDateTime deletadoEm) { this.deletadoEm = deletadoEm; }
    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }
}

@ApplicationScoped
class VersaoFichaTreinoJpaRepository implements PanacheRepositoryBase<VersaoFichaTreinoJpa, UUID> {}

/**
 * Adapter: Implementação JPA do repositório de Fichas de Treino.
 */
@ApplicationScoped
class FichaTreinoRepositoryImpl implements FichaTreinoRepository {

    @Inject
    FichaTreinoJpaRepository fichaRepo;

    @Override
    public FichaTreino salvar(FichaTreino ficha) {
        FichaTreinoJpa jpa = FichaTreinoJpa.fromDomain(ficha);
        fichaRepo.persistAndFlush(jpa);
        return jpa.toDomain();
    }

    @Override
    public Optional<FichaTreino> buscarPorId(UUID id) {
        return fichaRepo.findByIdOptional(id).map(FichaTreinoJpa::toDomain);
    }

    @Override
    public List<FichaTreino> listarPorUsuario(UUID usuarioId, int pagina, int tamanho) {
        return fichaRepo.find("usuarioId = ?1 ORDER BY criadoEm DESC", usuarioId)
                .page(pagina, tamanho).list().stream()
                .map(FichaTreinoJpa::toDomain).collect(Collectors.toList());
    }

    @Override
    public long contarPorUsuario(UUID usuarioId) {
        return fichaRepo.count("usuarioId = ?1", usuarioId);
    }
}
