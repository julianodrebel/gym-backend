package com.gymbackend.domain.repositories;

import com.gymbackend.domain.entities.ExercicioExecutado;
import com.gymbackend.domain.entities.SerieExecutada;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port (interface) do repositório de Exercícios Executados e Séries Executadas.
 */
public interface ExercicioExecutadoRepository {

    ExercicioExecutado salvar(ExercicioExecutado exercicioExecutado);

    SerieExecutada salvarSerie(SerieExecutada serie);

    Optional<ExercicioExecutado> buscarPorId(UUID id);

    List<ExercicioExecutado> listarPorSessao(UUID sessaoId);

    List<SerieExecutada> listarSeriesPorExercicioExecutado(UUID exercicioExecutadoId);

    /**
     * Busca últimas N séries de um exercício (para cálculo de progressão).
     */
    List<SerieExecutada> buscarUltimasSeriesPorExercicio(UUID exercicioId, int quantidade);

    /**
     * Busca séries de um exercício num período (para histórico).
     */
    List<SerieExecutada> buscarSeriesPorExercicioEPeriodo(
            UUID exercicioId, LocalDateTime inicio, LocalDateTime fim, int pagina, int tamanho);

    long contarSeriesPorExercicio(UUID exercicioId);

    Optional<SerieExecutada> buscarMelhorCargaPorExercicio(UUID exercicioId);

    Optional<ExercicioExecutado> buscarUltimaExecucaoPorExercicio(UUID exercicioId);
}
