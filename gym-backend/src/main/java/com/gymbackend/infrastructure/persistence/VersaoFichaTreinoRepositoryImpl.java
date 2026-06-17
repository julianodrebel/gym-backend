package com.gymbackend.infrastructure.persistence;

import com.gymbackend.domain.entities.VersaoFichaTreino;
import com.gymbackend.domain.repositories.VersaoFichaTreinoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter: Implementação JPA do repositório de Versões de Ficha de Treino.
 */
@ApplicationScoped
public class VersaoFichaTreinoRepositoryImpl implements VersaoFichaTreinoRepository {

    @Inject
    VersaoFichaTreinoJpaRepository jpaRepository;

    @Override
    public VersaoFichaTreino salvar(VersaoFichaTreino versao) {
        VersaoFichaTreinoJpa jpa = VersaoFichaTreinoJpa.fromDomain(versao);
        jpaRepository.persistAndFlush(jpa);
        return jpa.toDomain();
    }

    @Override
    public Optional<VersaoFichaTreino> buscarPorId(UUID id) {
        return jpaRepository.findByIdOptional(id).map(VersaoFichaTreinoJpa::toDomain);
    }

    @Override
    public Optional<VersaoFichaTreino> buscarPorFichaENumero(UUID fichaTreinoId, Integer numero) {
        return jpaRepository
                .find("fichaTreinoId = ?1 AND numero = ?2 AND deletadoEm IS NULL", fichaTreinoId, numero)
                .firstResultOptional()
                .map(VersaoFichaTreinoJpa::toDomain);
    }

    @Override
    public List<VersaoFichaTreino> listarPorFicha(UUID fichaTreinoId) {
        return jpaRepository
                .find("fichaTreinoId = ?1 ORDER BY numero ASC", fichaTreinoId)
                .list().stream()
                .map(VersaoFichaTreinoJpa::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public int contarVersoesPorFicha(UUID fichaTreinoId) {
        return (int) jpaRepository.count("fichaTreinoId = ?1", fichaTreinoId);
    }
}
