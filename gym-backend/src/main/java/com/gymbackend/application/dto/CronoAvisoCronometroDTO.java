package com.gymbackend.application.dto;

/**
 * DTO para metadados do cronômetro de descanso.
 */
public class CronoAvisoCronometroDTO {

    private int tempoDescansoRecomendado;
    private boolean iniciarCrono;
    private boolean avisoIntervaloInsuficiente;

    public CronoAvisoCronometroDTO() {}

    public CronoAvisoCronometroDTO(int tempoDescansoRecomendado, boolean iniciarCrono,
                                    boolean avisoIntervaloInsuficiente) {
        this.tempoDescansoRecomendado = tempoDescansoRecomendado;
        this.iniciarCrono = iniciarCrono;
        this.avisoIntervaloInsuficiente = avisoIntervaloInsuficiente;
    }

    public int getTempoDescansoRecomendado() { return tempoDescansoRecomendado; }
    public void setTempoDescansoRecomendado(int tempoDescansoRecomendado) { this.tempoDescansoRecomendado = tempoDescansoRecomendado; }

    public boolean isIniciarCrono() { return iniciarCrono; }
    public void setIniciarCrono(boolean iniciarCrono) { this.iniciarCrono = iniciarCrono; }

    public boolean isAvisoIntervaloInsuficiente() { return avisoIntervaloInsuficiente; }
    public void setAvisoIntervaloInsuficiente(boolean avisoIntervaloInsuficiente) { this.avisoIntervaloInsuficiente = avisoIntervaloInsuficiente; }
}
