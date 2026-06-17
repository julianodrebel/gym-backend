# Contrato de API: Exercícios

**Propósito**: Definir interface de API para gestão de exercícios

**Baseado em**: [data-model.md](../data-model.md)

---

## Endpoints

### POST /api/exercicios

**Objetivo**: Criar novo exercício

**Requisição**:
```json
{
  "nome": "Supino Reto",
  "descricao": "Exercício básico de peito com barra",
  "grupoMuscular": "Peito"
}
```

**Resposta (201 Created)**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "Supino Reto",
  "descricao": "Exercício básico de peito com barra",
  "grupoMuscular": "Peito",
  "status": "ATIVO",
  "criadoEm": "2026-06-17T14:30:00Z",
  "atualizadoEm": "2026-06-17T14:30:00Z"
}
```

**Validações**:
- `nome`: obrigatório, max 255 chars, não vazio
- `descricao`: obrigatória, max 1000 chars
- `grupoMuscular`: obrigatório, from enum

**Erros**:
- 400: Dados inválidos
- 401: Não autenticado
- 409: Exercício com nome duplicado

---

### GET /api/exercicios/:id

**Objetivo**: Obter detalhes de exercício com histórico

**Resposta (200 OK)**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "Supino Reto",
  "descricao": "Exercício básico de peito com barra",
  "grupoMuscular": "Peito",
  "status": "ATIVO",
  "criadoEm": "2026-06-17T14:30:00Z",
  "atualizadoEm": "2026-06-17T14:30:00Z",
  "historicoUtilizacao": {
    "primeiraExecucao": "2026-06-18T08:00:00Z",
    "ultimaExecucao": "2026-06-20T10:15:30Z",
    "totalSeries": 45
  }
}
```

**Erros**:
- 404: Exercício não encontrado
- 401: Não autenticado

---

### GET /api/exercicios

**Objetivo**: Listar todos os exercícios (filtrados por status)

**Query Parameters**:
- `status`: ATIVO | INATIVO (default: ATIVO)
- `grupoMuscular`: filtro por grupo (opcional)
- `page`: 0-based page number (default: 0)
- `size`: items per page (default: 50, max: 100)

**Resposta (200 OK)**:
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "nome": "Supino Reto",
      "descricao": "...",
      "grupoMuscular": "Peito",
      "status": "ATIVO",
      "criadoEm": "2026-06-17T14:30:00Z"
    }
  ],
  "totalElements": 42,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 50
}
```

---

### PUT /api/exercicios/:id

**Objetivo**: Atualizar exercício (nome ou descrição)

**Requisição**:
```json
{
  "nome": "Supino Reto (Atualizado)",
  "descricao": "Novo descritivo"
}
```

**Resposta (200 OK)**: Exercício atualizado

**Validações**:
- Campos editáveis: nome, descrição apenas
- Status não pode ser editado (usar endpoint específico)

**Erros**:
- 404: Exercício não encontrado
- 409: Duplicação de nome
- 400: Dados inválidos

---

### DELETE /api/exercicios/:id

**Objetivo**: Inativar exercício (soft delete)

**Resposta (204 No Content)**: Exercício marcado como INATIVO

**Comportamento**:
- Não deleta histórico
- Pode ser reativado posteriormient
- Não pode ser usado em fichas novas

**Erros**:
- 404: Exercício não encontrado
- 400: Exercício já inativo

---

### POST /api/exercicios/:id/reativar

**Objetivo**: Reativar exercício inativo

**Resposta (200 OK)**: Exercício com status ATIVO

**Erros**:
- 404: Exercício não encontrado
- 400: Exercício já ativo

---

## DTOs de Domínio

### ExercicioDTO
```java
record ExercicioDTO(
    UUID id,
    String nome,
    String descricao,
    String grupoMuscular,
    String status,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {}
```

### CriarExercicioDTO
```java
record CriarExercicioDTO(
    @NotBlank String nome,
    @NotBlank String descricao,
    @NotNull String grupoMuscular
) {}
```

### HistoricoExercicioDTO
```java
record HistoricoExercicioDTO(
    LocalDateTime primeiraExecucao,
    LocalDateTime ultimaExecucao,
    long totalSeries
) {}
```

---

## Códigos HTTP Padrão

| Código | Significado |
|--------|-------------|
| 200 | OK - Sucesso |
| 201 | Created - Recurso criado |
| 204 | No Content - Sucesso sem corpo |
| 400 | Bad Request - Dados inválidos |
| 401 | Unauthorized - Não autenticado |
| 404 | Not Found - Recurso não existe |
| 409 | Conflict - Duplicação/restrição |
| 500 | Internal Server Error |

---

## Autenticação

Todos os endpoints requerem header JWT:
```
Authorization: Bearer <jwt_token>
```

---

## Paginação

Padrão para endpoints que retornam listas:
```json
{
  "content": [...],
  "totalElements": 100,
  "totalPages": 2,
  "currentPage": 0,
  "pageSize": 50,
  "hasNext": true,
  "hasPrevious": false
}
```

---

## Tratamento de Erros

Resposta padrão de erro (400, 404, 409, etc.):
```json
{
  "timestamp": "2026-06-17T14:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Campo 'nome' é obrigatório",
  "path": "/api/exercicios"
}
```
