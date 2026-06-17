# Contrato de API: Fichas de Treino

**Propósito**: Definir interface de API para gestão de fichas e versionamento

---

## Endpoints

### POST /api/fichas

**Objetivo**: Criar nova ficha de treino (com V1 vazia)

**Requisição**:
```json
{
  "nome": "Push Pull Legs"
}
```

**Resposta (201 Created)**:
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "nome": "Push Pull Legs",
  "versaoAtiva": {
    "id": "770e8400-e29b-41d4-a716-446655440002",
    "numero": 1,
    "exercicios": [],
    "criadoEm": "2026-06-17T14:30:00Z"
  },
  "criadoEm": "2026-06-17T14:30:00Z"
}
```

---

### GET /api/fichas/:id

**Objetivo**: Obter ficha com versão ativa

**Resposta (200 OK)**:
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "nome": "Push Pull Legs",
  "versaoAtiva": {
    "id": "770e8400-e29b-41d4-a716-446655440002",
    "numero": 1,
    "exercicios": [
      { "id": "e1", "nome": "Supino Reto", "ordem": 1 },
      { "id": "e2", "nome": "Desenvolvimento Militar", "ordem": 2 }
    ],
    "criadoEm": "2026-06-17T14:30:00Z"
  }
}
```

---

### GET /api/fichas/:id/versoes

**Objetivo**: Listar todas as versões (inclusive deletadas)

**Resposta (200 OK)**:
```json
[
  {
    "numero": 3,
    "status": "IMUTÁVEL",
    "criadoEm": "2026-06-19T10:00:00Z",
    "deletadoEm": null,
    "ativa": false
  },
  {
    "numero": 2,
    "status": "IMUTÁVEL",
    "criadoEm": "2026-06-18T08:00:00Z",
    "deletadoEm": null,
    "ativa": false
  },
  {
    "numero": 1,
    "status": "IMUTÁVEL",
    "criadoEm": "2026-06-17T14:30:00Z",
    "deletadoEm": null,
    "ativa": true
  }
]
```

---

### POST /api/fichas/:id/versoes

**Objetivo**: Criar nova versão (V2, V3, etc.) por modificação

**Requisição**:
```json
{
  "exercicios": [
    { "exercicioId": "e1", "ordem": 1 },
    { "exercicioId": "e2", "ordem": 2 },
    { "exercicioId": "e3", "ordem": 3 }
  ]
}
```

**Resposta (201 Created)**:
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440003",
  "numero": 2,
  "exercicios": [...],
  "criadoEm": "2026-06-18T08:00:00Z",
  "status": "IMUTÁVEL"
}
```

**Comportamento**:
- Criação de V2 não modifica V1 (imutabilidade)
- V1 continua imutável e disponível
- Versão nova NOT automaticamente ativa

**Erros**:
- 400: Exercício inativo não pode ser adicionado
- 409: Versão antiga não pode ser modificada

---

### POST /api/fichas/:id/versoes/:numero/ativar

**Objetivo**: Ativar versão anterior

**Resposta (200 OK)**:
```json
{
  "versaoAtiva": {
    "numero": 1,
    "exercicios": [...],
    "status": "IMUTÁVEL"
  }
}
```

---

### POST /api/fichas/:id/versoes/:numero/duplicar

**Objetivo**: Duplicar versão criando nova versão com mesmos exercícios

**Resposta (201 Created)**:
```json
{
  "id": "990e8400-e29b-41d4-a716-446655440004",
  "numero": 4,
  "exercicios": [...],
  "criadoEm": "2026-06-20T10:00:00Z"
}
```

---

## Comportamentos de Imutabilidade

| Operação | Versão Antiga | Resultado |
|----------|---------------|-----------|
| Editar V1 | Versão ativa | ERRO (versões são imutáveis) |
| Deletar V1 | Versão ativa | ERRO (versões nunca são deletadas) |
| Criar V2 | V1 permanece intacta | ✅ V1 imutável, V2 nova |
| Ativar V1 | Anterior era V2 | ✅ V1 agora ativa |
| Duplicar V1 | V1 permanece intacta | ✅ V4 (nova) criada |

---

## DTOs

### FichaTreinoDTO
```java
record FichaTreinoDTO(
    UUID id,
    String nome,
    VersaoDTO versaoAtiva,
    LocalDateTime criadoEm
) {}
```

### VersaoDTO
```java
record VersaoDTO(
    UUID id,
    Integer numero,
    List<ExercicioSimpleDTO> exercicios,
    LocalDateTime criadoEm,
    LocalDateTime deletadoEm,
    String status
) {}
```

---

## Validações

- Nome da ficha: obrigatório, max 255 chars
- Exercícios na versão: list não vazia (min 1)
- Ordem de execução: única e sequencial
- Versão para ativar: deve existir e estar ativa

---

## Auditing

Cada operação gera log:
- Criar ficha
- Criar versão
- Ativar versão
- Duplicar versão

Logs incluem: timestamp, usuário, ID versão, ação
