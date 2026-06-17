package com.gymbackend.domain.repositories;

import com.gymbackend.domain.entities.Exercicio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port (interface) do repositório de Exercícios.
 * Define operações de persistência sem depender de tecnologia de storage.
 */
public interface ExercicioRepository {

    Exercicio salvar(Exercicio exercicio);

    Optional<Exercicio> buscarPorId(UUID id);

    List<Exercicio> listarAtivos(int pagina, int tamanho);

    List<Exercicio> listarTodos(int pagina, int tamanho);

    long contarAtivos();

    long contarTodos();

    boolean existePorNome(String nome);

    boolean existePorNomeEIdDiferente(String nome, UUID idExcluido);
}
