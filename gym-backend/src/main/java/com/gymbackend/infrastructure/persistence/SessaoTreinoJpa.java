package com.gymbackend.infrastructure.persistence;

import com.gymbackend.domain.entities.ExercicioExecutado;
import com.gymbackend.domain.entities.SerieExecutada;
import com.gymbackend.domain.entities.SessaoTreino;
import com.gymbackend.domain.repositories.ExercicioExecutadoRepository;
import com.gymbackend.domain.repositories.SessaoTreinoRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

// ===== Entidade JPA: SessaoTreino =====
@Entity
@Table(name = "sessoes_treino")
class SessaoTreinoJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "usuario_id", nullable = false, columnDefinition = "UUID")
    private UUID usuarioId;

    @Column(name = "versao_ficha_treino_id", nullable = false, columnDefinition = "UUID")
    private UUID versaoFichaTreinoId;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "hora_inicio", nullable = false)
    private LocalDateTime horaInicio;

    @Column(name = "hora_fim")
    private LocalDateTime horaFim;

    @Column(name = "observacoes_gerais", length = 2000)
    private String observacoesGerais;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SessaoTreino.StatusSessao status;

    @Version
    @Column(name = "versao", nullable = false)
    private Long versao;

    public SessaoTreinoJpa() {}

    public static SessaoTreinoJpa fromDomain(SessaoTreino s) {
        SessaoTreinoJpa jpa = new SessaoTreinoJpa();
        jpa.setId(s.getId());
        jpa.setUsuarioId(s.getUsuarioId());
        jpa.setVersaoFichaTreinoId(s.getVersaoFichaTreinoId());
        jpa.setData(s.getData());
        jpa.setHoraInicio(s.getHoraInicio());
        jpa.setHoraFim(s.getHoraFim());
        jpa.setObservacoesGerais(s.getObservacoesGerais());
        jpa.setStatus(s.getStatus());
        jpa.setVersao(s.getVersao() != null ? s.getVersao() : 0L);
        return jpa;
    }

    public SessaoTreino toDomain() {
        SessaoTreino s = new SessaoTreino();
        s.setId(id);
        s.setUsuarioId(usuarioId);
        s.setVersaoFichaTreinoId(versaoFichaTreinoId);
        s.setData(data);
        s.setHoraInicio(horaInicio);
        s.setHoraFim(horaFim);
        s.setObservacoesGerais(observacoesGerais);
        s.setStatus(status);
        s.setVersao(versao);
        return s;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public UUID getVersaoFichaTreinoId() { return versaoFichaTreinoId; }
    public void setVersaoFichaTreinoId(UUID v) { this.versaoFichaTreinoId = v; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalDateTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalDateTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalDateTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalDateTime horaFim) { this.horaFim = horaFim; }
    public String getObservacoesGerais() { return observacoesGerais; }
    public void setObservacoesGerais(String obs) { this.observacoesGerais = obs; }
    public SessaoTreino.StatusSessao getStatus() { return status; }
    public void setStatus(SessaoTreino.StatusSessao status) { this.status = status; }
    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }
}

@ApplicationScoped
class SessaoTreinoJpaRepository implements PanacheRepositoryBase<SessaoTreinoJpa, UUID> {}

// ===== Entidade JPA: ExercicioExecutado =====
@Entity
@Table(name = "exercicios_executados")
class ExercicioExecutadoJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "sessao_id", nullable = false, columnDefinition = "UUID")
    private UUID sessaoId;

    @Column(name = "exercicio_id", nullable = false, columnDefinition = "UUID")
    private UUID exercicioId;

    @Column(name = "ordem", nullable = false)
    private Integer ordem;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    public ExercicioExecutadoJpa() {}

    public static ExercicioExecutadoJpa fromDomain(ExercicioExecutado e) {
        ExercicioExecutadoJpa jpa = new ExercicioExecutadoJpa();
        jpa.setId(e.getId());
        jpa.setSessaoId(e.getSessaoId());
        jpa.setExercicioId(e.getExercicioId());
        jpa.setOrdem(e.getOrdem());
        jpa.setObservacoes(e.getObservacoes());
        jpa.setDuracaoMinutos(e.getDuracaoMinutos());
        return jpa;
    }

    public ExercicioExecutado toDomain() {
        ExercicioExecutado e = new ExercicioExecutado();
        e.setId(id);
        e.setSessaoId(sessaoId);
        e.setExercicioId(exercicioId);
        e.setOrdem(ordem);
        e.setObservacoes(observacoes);
        e.setDuracaoMinutos(duracaoMinutos);
        return e;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSessaoId() { return sessaoId; }
    public void setSessaoId(UUID sessaoId) { this.sessaoId = sessaoId; }
    public UUID getExercicioId() { return exercicioId; }
    public void setExercicioId(UUID exercicioId) { this.exercicioId = exercicioId; }
    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Integer getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(Integer duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
}

@ApplicationScoped
class ExercicioExecutadoJpaRepository implements PanacheRepositoryBase<ExercicioExecutadoJpa, UUID> {}

// ===== Entidade JPA: SerieExecutada =====
@Entity
@Table(name = "series_executadas")
class SerieExecutadaJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "exercicio_executado_id", nullable = false, columnDefinition = "UUID")
    private UUID exercicioExecutadoId;

    @Column(name = "numero_serie", nullable = false)
    private Integer numeroSerie;

    @Column(name = "peso", nullable = false, precision = 8, scale = 2)
    private BigDecimal peso;

    @Column(name = "repeticoes", nullable = false)
    private Integer repeticoes;

    @Column(name = "horario_execucao", nullable = false)
    private LocalDateTime horarioExecucao;

    @Version
    @Column(name = "versao", nullable = false)
    private Long versao;

    public SerieExecutadaJpa() {}

    public static SerieExecutadaJpa fromDomain(SerieExecutada s) {
        SerieExecutadaJpa jpa = new SerieExecutadaJpa();
        jpa.setId(s.getId());
        jpa.setExercicioExecutadoId(s.getExercicioExecutadoId());
        jpa.setNumeroSerie(s.getNumeroSerie());
        jpa.setPeso(s.getPeso());
        jpa.setRepeticoes(s.getRepeticoes());
        jpa.setHorarioExecucao(s.getHorarioExecucao());
        jpa.setVersao(s.getVersao() != null ? s.getVersao() : 0L);
        return jpa;
    }

    public SerieExecutada toDomain() {
        SerieExecutada s = new SerieExecutada();
        s.setId(id);
        s.setExercicioExecutadoId(exercicioExecutadoId);
        s.setNumeroSerie(numeroSerie);
        s.setPeso(peso);
        s.setRepeticoes(repeticoes);
        s.setHorarioExecucao(horarioExecucao);
        s.setVersao(versao);
        return s;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getExercicioExecutadoId() { return exercicioExecutadoId; }
    public void setExercicioExecutadoId(UUID id) { this.exercicioExecutadoId = id; }
    public Integer getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(Integer n) { this.numeroSerie = n; }
    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }
    public Integer getRepeticoes() { return repeticoes; }
    public void setRepeticoes(Integer repeticoes) { this.repeticoes = repeticoes; }
    public LocalDateTime getHorarioExecucao() { return horarioExecucao; }
    public void setHorarioExecucao(LocalDateTime h) { this.horarioExecucao = h; }
    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }
}

@ApplicationScoped
class SerieExecutadaJpaRepository implements PanacheRepositoryBase<SerieExecutadaJpa, UUID> {}

// ===== Implementação dos Repositórios =====

@ApplicationScoped
class SessaoTreinoRepositoryImpl implements SessaoTreinoRepository {

    @Inject SessaoTreinoJpaRepository jpaRepo;

    @Override
    public SessaoTreino salvar(SessaoTreino sessao) {
        SessaoTreinoJpa jpa = SessaoTreinoJpa.fromDomain(sessao);
        jpaRepo.persistAndFlush(jpa);
        return jpa.toDomain();
    }

    @Override
    public Optional<SessaoTreino> buscarPorId(UUID id) {
        return jpaRepo.findByIdOptional(id).map(SessaoTreinoJpa::toDomain);
    }

    @Override
    public List<SessaoTreino> listarPorUsuario(UUID usuarioId, int pagina, int tamanho) {
        return jpaRepo.find("usuarioId = ?1 ORDER BY horaInicio DESC", usuarioId)
                .page(pagina, tamanho).list().stream()
                .map(SessaoTreinoJpa::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<SessaoTreino> buscarUltimaSessaoPorUsuario(UUID usuarioId) {
        return jpaRepo.find("usuarioId = ?1 ORDER BY horaInicio DESC", usuarioId)
                .firstResultOptional().map(SessaoTreinoJpa::toDomain);
    }

    @Override
    public List<SessaoTreino> buscarPorUsuarioEPeriodo(UUID usuarioId, LocalDate inicio, LocalDate fim) {
        return jpaRepo.find("usuarioId = ?1 AND data >= ?2 AND data <= ?3 ORDER BY data DESC",
                usuarioId, inicio, fim)
                .list().stream().map(SessaoTreinoJpa::toDomain).collect(Collectors.toList());
    }
}

@ApplicationScoped
class ExercicioExecutadoRepositoryImpl implements ExercicioExecutadoRepository {

    @Inject ExercicioExecutadoJpaRepository execRepo;
    @Inject SerieExecutadaJpaRepository serieRepo;

    @Override
    public ExercicioExecutado salvar(ExercicioExecutado e) {
        ExercicioExecutadoJpa jpa = ExercicioExecutadoJpa.fromDomain(e);
        execRepo.persistAndFlush(jpa);
        return jpa.toDomain();
    }

    @Override
    public SerieExecutada salvarSerie(SerieExecutada serie) {
        SerieExecutadaJpa jpa = SerieExecutadaJpa.fromDomain(serie);
        serieRepo.persistAndFlush(jpa);
        return jpa.toDomain();
    }

    @Override
    public Optional<ExercicioExecutado> buscarPorId(UUID id) {
        return execRepo.findByIdOptional(id).map(ExercicioExecutadoJpa::toDomain);
    }

    @Override
    public List<ExercicioExecutado> listarPorSessao(UUID sessaoId) {
        return execRepo.find("sessaoId = ?1 ORDER BY ordem ASC", sessaoId)
                .list().stream().map(ExercicioExecutadoJpa::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<SerieExecutada> listarSeriesPorExercicioExecutado(UUID execId) {
        return serieRepo.find("exercicioExecutadoId = ?1 ORDER BY numeroSerie ASC", execId)
                .list().stream().map(SerieExecutadaJpa::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<SerieExecutada> buscarUltimasSeriesPorExercicio(UUID exercicioId, int quantidade) {
        return serieRepo.find(
                "exercicioExecutadoId IN (SELECT e.id FROM ExercicioExecutadoJpa e WHERE e.exercicioId = ?1) ORDER BY horarioExecucao DESC",
                exercicioId)
                .page(0, quantidade)
                .list().stream().map(SerieExecutadaJpa::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<SerieExecutada> buscarSeriesPorExercicioEPeriodo(
            UUID exercicioId, LocalDateTime inicio, LocalDateTime fim, int pagina, int tamanho) {
        return serieRepo.find(
                "exercicioExecutadoId IN (SELECT e.id FROM ExercicioExecutadoJpa e WHERE e.exercicioId = ?1) AND horarioExecucao >= ?2 AND horarioExecucao <= ?3 ORDER BY horarioExecucao DESC",
                exercicioId, inicio, fim)
                .page(pagina, tamanho)
                .list().stream().map(SerieExecutadaJpa::toDomain).collect(Collectors.toList());
    }

    @Override
    public long contarSeriesPorExercicio(UUID exercicioId) {
        return serieRepo.count(
                "exercicioExecutadoId IN (SELECT e.id FROM ExercicioExecutadoJpa e WHERE e.exercicioId = ?1)",
                exercicioId);
    }

    @Override
    public Optional<SerieExecutada> buscarMelhorCargaPorExercicio(UUID exercicioId) {
        return serieRepo.find(
                "exercicioExecutadoId IN (SELECT e.id FROM ExercicioExecutadoJpa e WHERE e.exercicioId = ?1) ORDER BY peso DESC",
                exercicioId)
                .firstResultOptional()
                .map(SerieExecutadaJpa::toDomain);
    }

    @Override
    public Optional<ExercicioExecutado> buscarUltimaExecucaoPorExercicio(UUID exercicioId) {
        return execRepo.find(
                "exercicioId = ?1 ORDER BY id DESC", exercicioId)
                .firstResultOptional()
                .map(ExercicioExecutadoJpa::toDomain);
    }
}
