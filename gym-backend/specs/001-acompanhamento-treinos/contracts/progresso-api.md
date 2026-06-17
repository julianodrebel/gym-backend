# Contrato de API: Sugestão de Progressão

**Propósito**: Definir interface de API para sugestões automáticas

---

## Endpoints

### GET /api/exercicios/:id/progressao

**Objetivo**: Obter sugestão de progressão baseada no histórico

**Resposta (200 OK)**:
```json
{
  "exercicioId": "e1",
  "exercicioNome": "Supino Reto",
  "sugestao": "AUMENTAR_CARGA",
  "detalhes": {
    "cargaAtual": 60.0,
    "cargaSugerida": 62.5,
    "aumento": "2.5kg (+4.2%)",
    "justificativa": "Todas as séries da última sessão atingiram 12 reps"
  },
  "ultimasSeriesConsideradas": [
    { "peso": 60, "repeticoes": 12, "data": "2026-06-20" },
    { "peso": 60, "repeticoes": 12, "data": "2026-06-19" },
    { "peso": 60, "repeticoes": 11, "data": "2026-06-18" }
  ]
}
```

---

### GET /api/exercicios/:id/progressao (sem histórico)

**Resposta (200 OK)**:
```json
{
  "exercicioId": "e1",
  "sugestao": "SEM_HISTORICO",
  "mensagem": "Comece com carga confortável (exercício sem registros)",
  "ultimasSeriesConsideradas": []
}
```

---

## Tipos de Sugestão

| Sugestão | Condição | Ação |
|----------|----------|------|
| AUMENTAR_CARGA | Todas séries ≥ 12 reps | Aumentar 2.5-5kg |
| MANTER_CARGA | 8 ≤ reps ≤ 12 | Manter, focar em reps |
| MANTER_CARGA | Alguma série < 8 reps | Manter, atingir 8 mínimo |
| SEM_HISTORICO | 0 séries | Começar confortável |

---

## DTOs

### ProgressaoDTO
```java
record ProgressaoDTO(
    UUID exercicioId,
    String exercicioNome,
    String sugestao,
    DetalhesProgressaoDTO detalhes,
    List<SerieHistoricoDTO> ultimasSeriesConsideradas
) {}
```

### DetalhesProgressaoDTO
```java
record DetalhesProgressaoDTO(
    BigDecimal cargaAtual,
    BigDecimal cargaSugerida,
    String aumento,
    String justificativa
) {}
```

---

## Algoritmo

```java
public ProgressaoDTO calcularProgressao(UUID exercicioId) {
    List<SerieExecutada> ultimas5Series = 
        repositorio.obterUltimas5Series(exercicioId);
    
    if (ultimas5Series.isEmpty()) {
        return ProgressaoDTO.semHistorico(exercicioId);
    }
    
    double minReps = ultimas5Series.stream()
        .mapToDouble(SerieExecutada::getRepeticoes)
        .min().orElse(0);
    
    BigDecimal cargaAtual = ultimas5Series.get(0).getPeso();
    
    if (minReps >= 12) {
        return ProgressaoDTO.aumentarCarga(
            cargaAtual,
            cargaAtual.multiply(1.05) // +5%
        );
    } else if (minReps >= 8) {
        return ProgressaoDTO.manterCargaProgressarReps(
            cargaAtual
        );
    } else {
        return ProgressaoDTO.manterCarga(cargaAtual);
    }
}
```

---

## Regras

1. Considera últimas 5 séries (última semana aproximadamente)
2. Se mínimo de reps ≥ 12 → aumentar
3. Se mínimo < 8 → manter
4. Se 8 ≤ min < 12 → manter e progredir reps
5. Sem histórico → sugerir carga confortável
