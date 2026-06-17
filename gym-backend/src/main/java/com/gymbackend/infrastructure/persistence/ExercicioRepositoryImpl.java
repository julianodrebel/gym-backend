package com.gymbackend.infrastructure.persistence;

import com.gymbackend.domain.entities.Exercicio;
import com.gymbackend.domain.repositories.ExercicioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter: Implementação JPA do repositório de Exercícios.
 * Converte entre entidades JPA e entidades de domínio.
 */
@ApplicationScoped
public class ExercicioRepositoryImpl implements ExercicioRepository {

    @Inject
    ExercicioJpaRepository jpaRepository;

    @Override
    public Exercicio salvar(Exercicio exercicio) {
        ExercicioJpa jpa = ExercicioJpa.fromDomain(exercicio);
        jpaRepository.persistAndFlush(jpa);
        return jpa.toDomain();
    }

    @Override
    public Optional<Exercicio> buscarPorId(UUID id) {
        return jpaRepository.findByIdOptional(id)
                .map(ExercicioJpa::toDomain);
    }

    @Override
    public List<Exercicio> listarAtivos(int pagina, int tamanho) {
        return jpaRepository
                .find("status = ?1 AND deletadoEm IS NULL ORDER BY nome",
                        Exercicio.StatusExercicio.ATIVO)
                .page(pagina, tamanho)
                .list()
                .stream()
                .map(ExercicioJpa::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Exercicio> listarTodos(int pagina, int tamanho) {
        return jpaRepository
                .findAll()
                .page(pagina, tamanho)
                .list()
                .stream()
                .map(ExercicioJpa::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long contarAtivos() {
        return jpaRepository.count("status = ?1 AND deletadoEm IS NULL",
                Exercicio.StatusExercicio.ATIVO);
    }

    @Override
    public long contarTodos() {
        return jpaRepository.count();
    }

    @Override
    public boolean existePorNome(String nome) {
        return jpaRepository.count(
                "nome = ?1 AND status = ?2 AND deletadoEm IS NULL",
                nome, Exercicio.StatusExercicio.ATIVO) > 0;
    }

    @Override
    public boolean existePorNomeEIdDiferente(String nome, UUID idExcluido) {
        return jpaRepository.count(
                "nome = ?1 AND id != ?2 AND status = ?3 AND deletadoEm IS NULL",
                nome, idExcluido, Exercicio.StatusExercicio.ATIVO) > 0;
    }
}
