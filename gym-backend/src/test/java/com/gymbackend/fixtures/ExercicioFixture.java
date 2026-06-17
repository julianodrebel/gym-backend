package com.gymbackend.fixtures;

import com.gymbackend.domain.entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Data builders para fixtures de entidades de domínio.
 * Simplifica criação de objetos para testes.
 */
public class ExercicioFixture {

    private static final UUID ID_PADRAO = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID ID_USUARIO = UUID.fromString("00000000-0000-0000-0000-000000000001");

    /**
     * Cria um Exercicio válido com dados padrão.
     */
    public static Exercicio exercicioValido() {
        return new Exercicio("Supino Reto", "Exercício para peitoral", "Peito");
    }

    /**
     * Cria um Exercicio com nome específico.
     */
    public static Exercicio exercicioComNome(String nome) {
        return new Exercicio(nome, "Descrição padrão", "Peito");
    }

    /**
     * Cria uma FichaTreino válida.
     */
    public static FichaTreino fichaValida() {
        FichaTreino ficha = new FichaTreino();
        ficha.setId(UUID.fromString("660e8400-e29b-41d4-a716-446655440001"));
        ficha.setUsuarioId(ID_USUARIO);
        ficha.setNome("Treino A - Peito e Tríceps");
        ficha.setCriadoEm(LocalDateTime.now());
        ficha.setAtualizadoEm(LocalDateTime.now());
        return ficha;
    }

    /**
     * Cria uma VersaoFichaTreino válida.
     */
    public static VersaoFichaTreino versaoValida(UUID fichaTreinoId) {
        VersaoFichaTreino versao = new VersaoFichaTreino();
        versao.setId(UUID.fromString("770e8400-e29b-41d4-a716-446655440002"));
        versao.setFichaTreinoId(fichaTreinoId);
        versao.setNumero(1);
        versao.setCriadoEm(LocalDateTime.now());
        return versao;
    }

    /**
     * Cria uma SessaoTreino válida em progresso.
     */
    public static SessaoTreino sessaoEmProgresso(UUID versaoId) {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setId(UUID.fromString("880e8400-e29b-41d4-a716-446655440003"));
        sessao.setUsuarioId(ID_USUARIO);
        sessao.setVersaoFichaTreinoId(versaoId);
        sessao.setData(LocalDate.now());
        sessao.setHoraInicio(LocalDateTime.now());
        sessao.setStatus(SessaoTreino.StatusSessao.EM_PROGRESSO);
        sessao.setExercicios(new ArrayList<>());
        return sessao;
    }

    /**
     * Cria uma SerieExecutada válida.
     */
    public static SerieExecutada serieValida(UUID exercicioExecutadoId) {
        return new SerieExecutada(exercicioExecutadoId, 1, new BigDecimal("60.0"), 10);
    }

    /**
     * Cria uma SerieExecutada com peso e reps customizados.
     */
    public static SerieExecutada serieComCarga(UUID execId, BigDecimal peso, int reps) {
        return new SerieExecutada(execId, 1, peso, reps);
    }
}
