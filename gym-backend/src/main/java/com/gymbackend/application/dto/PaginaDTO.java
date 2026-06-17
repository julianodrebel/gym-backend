package com.gymbackend.application.dto;

import java.util.List;

/**
 * DTO genérico para respostas paginadas.
 */
public class PaginaDTO<T> {

    private List<T> conteudo;
    private int totalElementos;
    private int totalPaginas;
    private int paginaAtual;
    private int tamanhoPagina;
    private boolean temProxima;

    public PaginaDTO() {}

    public PaginaDTO(List<T> conteudo, long totalElementos, int pagina, int tamanho) {
        this.conteudo = conteudo;
        this.totalElementos = (int) totalElementos;
        this.tamanhoPagina = tamanho;
        this.paginaAtual = pagina;
        this.totalPaginas = tamanho > 0 ? (int) Math.ceil((double) totalElementos / tamanho) : 0;
        this.temProxima = pagina < totalPaginas - 1;
    }

    public List<T> getConteudo() { return conteudo; }
    public void setConteudo(List<T> conteudo) { this.conteudo = conteudo; }

    public int getTotalElementos() { return totalElementos; }
    public void setTotalElementos(int totalElementos) { this.totalElementos = totalElementos; }

    public int getTotalPaginas() { return totalPaginas; }
    public void setTotalPaginas(int totalPaginas) { this.totalPaginas = totalPaginas; }

    public int getPaginaAtual() { return paginaAtual; }
    public void setPaginaAtual(int paginaAtual) { this.paginaAtual = paginaAtual; }

    public int getTamanhoPagina() { return tamanhoPagina; }
    public void setTamanhoPagina(int tamanhoPagina) { this.tamanhoPagina = tamanhoPagina; }

    public boolean isTemProxima() { return temProxima; }
    public void setTemProxima(boolean temProxima) { this.temProxima = temProxima; }
}
