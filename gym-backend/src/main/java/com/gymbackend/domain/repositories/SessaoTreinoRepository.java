package com.gymbackend.domain.repositories;

import com.gymbackend.domain.entities.SessaoTreino;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port (interface) do repositório de Sessões de Treino.
 */
public interface SessaoTreinoRepository {

    SessaoTreino salvar(SessaoTreino sessao);

    Optional<SessaoTreino> buscarPorId(UUID id);

    List<SessaoTreino> listarPorUsuario(UUID usuarioId, int pagina, int tamanho);

    Optional<SessaoTreino> buscarUltimaSessaoPorUsuario(UUID usuarioId);

    List<SessaoTreino> buscarPorUsuarioEPeriodo(UUID usuarioId, LocalDate inicio, LocalDate fim);
}
