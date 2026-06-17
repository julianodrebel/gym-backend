package com.gymbackend.domain.repositories;

import com.gymbackend.domain.entities.FichaTreino;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port (interface) do repositório de Fichas de Treino.
 */
public interface FichaTreinoRepository {

    FichaTreino salvar(FichaTreino fichaTreino);

    Optional<FichaTreino> buscarPorId(UUID id);

    List<FichaTreino> listarPorUsuario(UUID usuarioId, int pagina, int tamanho);

    long contarPorUsuario(UUID usuarioId);
}
