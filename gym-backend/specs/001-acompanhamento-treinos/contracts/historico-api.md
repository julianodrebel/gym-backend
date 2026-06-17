# Contrato de API: Histórico e Desempenho

**Propósito**: Definir interface de API para consultas de histórico

---

## Endpoints

### GET /api/exercicios/:id/historico

**Objetivo**: Obter histórico completo de séries de exercício (paginado)

**Query Parameters**:
- `page`: 0-based (default: 0)
- `size`: items per page (default: 50, max: 500)
- `dataInicio`: filtro data inicial (opcional)
- `dataFim`: filtro data final (opcional)

**Resposta (200 OK)**:
```json
{
  "content": [
    {
      "id": "s1",
      "numeroSerie": 3,
      "peso": 60.0,
      "repeticoes": 8,
      "volume": 480,
      "horarioExecucao": "2026-06-20T08:11:00Z",
      "sessaoTreinoId": "aa0e8400-e29b-41d4-a716-446655440005"
    },
    {
      "id": "s2",
      "numeroSerie": 2,
      "peso": 60.0,
      "repeticoes": 9,
      "volume": 540,
      "horarioExecucao": "2026-06-20T08:08:00Z",
      "sessaoTreinoId": "aa0e8400-e29b-41d4-a716-446655440005"
    }
  ],
  "totalElements": 127,
  "totalPages": 3,
  "currentPage": 0,
  "pageSize": 50,
  "hasNext": true
}
```

---

### GET /api/exercicios/:id/evolucao

**Objetivo**: Obter evolução de carga (agrupado por sessão)

**Resposta (200 OK)**:
```json
{
  "exercicioId": "e1",
  "exercicioNome": "Supino Reto",
  "evolucao": [
    {
      "data": "2026-06-18",
      "cargaMaxima": 55.0,
      "volumeTotal": 1320, // peso * reps * series
      "numeroSeries": 3,
      "repsMedia": 9.3,
      "sessaoId": "aa0e8400-e29b-41d4-a716-446655440003"
    },
    {
      "data": "2026-06-19",
      "cargaMaxima": 57.5,
      "volumeTotal": 1437.5,
      "numeroSeries": 3,
      "repsMedia": 9.7,
      "sessaoId": "aa0e8400-e29b-41d4-a716-446655440004"
    },
    {
      "data": "2026-06-20",
      "cargaMaxima": 60.0,
      "volumeTotal": 1620,
      "numeroSeries": 3,
      "repsMedia": 9.0,
      "sessaoId": "aa0e8400-e29b-41d4-a716-446655440005"
    }
  ],
  "tendencia": "CRESCENTE",
  "progressaoCarga": "+5kg em 2 semanas",
  "progressaoVolume": "+300 volume em 2 semanas"
}
```

---

### GET /api/exercicios/:id/ultima-execucao

**Objetivo**: Obter dados da última execução de exercício

**Resposta (200 OK)**:
```json
{
  "data": "2026-06-20",
  "cargaMaxima": 60.0,
  "numeroSeries": 3,
  "repeticoes": [10, 9, 8],
  "repeticoesMedia": 9.0,
  "volumeTotal": 1620,
  "observacoes": "Ótima sensação",
  "sessaoId": "aa0e8400-e29b-41d4-a716-446655440005"
}
```

---

### GET /api/exercicios/:id/melhor-carga

**Objetivo**: Obter melhor carga registrada para exercício

**Resposta (200 OK)**:
```json
{
  "cargaMaxima": 65.0,
  "data": "2026-06-12",
  "repeticoes": 8,
  "repeticoesMaiores": 12,
  "sessaoId": "aa0e8400-e29b-41d4-a716-446655440000"
}
```

---

### GET /api/exercicios/:id/comparacao

**Objetivo**: Comparar desempenho entre dois períodos

**Query Parameters**:
- `dataPeriodo1Inicio`: início período 1
- `dataPeriodo1Fim`: fim período 1
- `dataPeriodo2Inicio`: início período 2
- `dataPeriodo2Fim`: fim período 2

**Resposta (200 OK)**:
```json
{
  "exercicioId": "e1",
  "periodo1": {
    "label": "2026-06-01 a 2026-06-12",
    "cargaMedia": 50.0,
    "volumeMedia": 1200,
    "numeroSessoes": 4
  },
  "periodo2": {
    "label": "2026-06-13 a 2026-06-20",
    "cargaMedia": 58.75,
    "volumeMedia": 1514,
    "numeroSessoes": 4
  },
  "melhora": {
    "carga": "+8.75kg (+17.5%)",
    "volume": "+314 (+26.2%)"
  }
}
```

---

## DTOs

### SerieHistoricoDTO
```java
record SerieHistoricoDTO(
    UUID id,
    Integer numeroSerie,
    BigDecimal peso,
    Integer repeticoes,
    Integer volume, // peso * repeticoes
    LocalDateTime horarioExecucao,
    UUID sessaoTreinoId
) {}
```

### EvolucaoDTO
```java
record EvolucaoDTO(
    LocalDate data,
    BigDecimal cargaMaxima,
    Integer volumeTotal,
    Integer numeroSeries,
    Double repsMedia,
    UUID sessaoId
) {}
```

### ComparacaoDTO
```java
record ComparacaoDTO(
    UUID exercicioId,
    PeriodoStatsDTO periodo1,
    PeriodoStatsDTO periodo2,
    MelhoraDTO melhora
) {}
```

---

## Cálculos

### Volume
```
Volume = Peso × Repetições × Número de Séries
Exemplo: 60kg × 10 reps × 3 séries = 1800 volume
```

### Tendência
- CRESCENTE: carga máxima cresce semana a semana
- ESTÁVEL: carga mantém-se ± 2.5kg
- DECRESCENTE: carga diminui

### Paginação
- Default: 50 itens
- Max: 500 itens
- Order by: `horarioExecucao DESC` (mais recente primeiro)

---

## Validações

- Data início < Data fim
- Datas devem estar no formato ISO (YYYY-MM-DD)
- Exercício deve existir e estar ATIVO ou ter histórico

---

## Performance

- Histórico com paginação: < 500ms
- Evolução agregada: < 1000ms (calcula no-the-fly)
- Índices: `(exercicio_id, horario_execucao DESC)`
