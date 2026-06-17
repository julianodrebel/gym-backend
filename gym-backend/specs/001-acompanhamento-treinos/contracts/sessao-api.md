# Contrato de API: Sessões de Treino

**Propósito**: Definir interface de API para execução de treinos

---

## Endpoints

### POST /api/sessoes

**Objetivo**: Iniciar novo treino (cria sessão EM_PROGRESSO)

**Requisição**:
```json
{
  "fichaId": "660e8400-e29b-41d4-a716-446655440001"
}
```

**Resposta (201 Created)**:
```json
{
  "id": "aa0e8400-e29b-41d4-a716-446655440005",
  "versaoFichaTreinoId": "770e8400-e29b-41d4-a716-446655440002",
  "data": "2026-06-20",
  "horaInicio": "08:00:00",
  "status": "EM_PROGRESSO",
  "exercicios": [
    {
      "id": "ex1",
      "exercicioId": "e1",
      "nome": "Supino Reto",
      "ordem": 1,
      "series": [],
      "observacoes": null
    }
  ],
  "criadoEm": "2026-06-20T08:00:00Z"
}
```

---

### GET /api/sessoes/:id

**Objetivo**: Obter sessão com todas as séries

**Resposta (200 OK)**:
```json
{
  "id": "aa0e8400-e29b-41d4-a716-446655440005",
  "data": "2026-06-20",
  "horaInicio": "08:00:00",
  "horaFim": "09:15:00",
  "duracao": "1h 15min",
  "status": "FINALIZADO",
  "observacoesGerais": "Treino bom, pouca energia",
  "exercicios": [
    {
      "id": "ex1",
      "exercicioId": "e1",
      "nome": "Supino Reto",
      "ordem": 1,
      "series": [
        { "numero": 1, "peso": 60, "repeticoes": 10, "horarioExecucao": "08:05:00" },
        { "numero": 2, "peso": 60, "repeticoes": 9, "horarioExecucao": "08:08:00" },
        { "numero": 3, "peso": 60, "repeticoes": 8, "horarioExecucao": "08:11:00" }
      ],
      "observacoes": "Ótima sensação"
    }
  ]
}
```

---

### POST /api/sessoes/:id/exercicios/:exercicioExecId/series

**Objetivo**: Registrar série durante execução

**Requisição**:
```json
{
  "peso": 60.0,
  "repeticoes": 10,
  "observacoes": "Sentiu bem"
}
```

**Resposta (201 Created)**:
```json
{
  "id": "bb0e8400-e29b-41d4-a716-446655440006",
  "numeroSerie": 1,
  "peso": 60.0,
  "repeticoes": 10,
  "horarioExecucao": "2026-06-20T08:05:00Z",
  "cronoAvisoCronometro": {
    "tempoDescansoRecomendado": 60,
    "iniciarCronom": true
  }
}
```

**Validações**:
- Peso: >= 0, <= 500kg
- Repetições: >= 0, <= 100
- Série deve ser consecutiva (1, 2, 3...)

---

### PUT /api/sessoes/:id/exercicios/:exercicioExecId/observacoes

**Objetivo**: Adicionar observações de exercício

**Requisição**:
```json
{
  "observacoes": "Dor no ombro ao final"
}
```

**Resposta (200 OK)**: Exercício com observações atualizadas

---

### PUT /api/sessoes/:id/finalizar

**Objetivo**: Finalizar sessão e marcar como FINALIZADO

**Requisição**:
```json
{
  "observacoesGerais": "Treino produtivo, poucaseria fácil"
}
```

**Resposta (200 OK)**:
```json
{
  "id": "aa0e8400-e29b-41d4-a716-446655440005",
  "status": "FINALIZADO",
  "horaFim": "2026-06-20T09:15:00Z",
  "duracao": "1h 15min"
}
```

---

### PUT /api/sessoes/:id/cancelar

**Objetivo**: Cancelar sessão em progresso

**Resposta (200 OK)**:
```json
{
  "status": "CANCELADO",
  "mensagem": "Sessão cancelada com sucesso"
}
```

---

## DTOs

### SessaoDTO
```java
record SessaoDTO(
    UUID id,
    LocalDate data,
    LocalTime horaInicio,
    LocalTime horaFim,
    String status,
    String observacoesGerais,
    List<ExercicioExecutadoDTO> exercicios
) {}
```

### SerieDTO
```java
record SerieDTO(
    UUID id,
    Integer numeroSerie,
    BigDecimal peso,
    Integer repeticoes,
    LocalDateTime horarioExecucao
) {}
```

---

## Estados

- EM_PROGRESSO → FINALIZADO (sucesso)
- EM_PROGRESSO → CANCELADO (abortada)
- Transições inversas: ERRO

---

## Cronômetro

Após cada série confirmada:
```json
{
  "cronoAvisoCronometro": {
    "tempoDescansoRecomendado": 60,
    "iniciarCrono": true,
    "mensagem": "Descanso iniciado"
  }
}
```

Tempo padrão: 60s (configurável por exercício)
