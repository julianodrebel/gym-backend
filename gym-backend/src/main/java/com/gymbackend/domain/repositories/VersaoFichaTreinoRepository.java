package com.gymbackend.domain.repositories;

import com.gymbackend.domain.entities.VersaoFichaTreino;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port (interface) do repositório de Versões de Ficha de Treino.
 */
public interface VersaoFichaTreinoRepository {

    VersaoFichaTreino salvar(VersaoFichaTreino versao);

    Optional<VersaoFichaTreino> buscarPorId(UUID id);

    Optional<VersaoFichaTreino> buscarPorFichaENumero(UUID fichaTreinoId, Integer numero);

    List<VersaoFichaTreino> listarPorFicha(UUID fichaTreinoId);

    int contarVersoesPorFicha(UUID fichaTreinoId);
}
