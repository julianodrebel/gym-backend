# Gym Backend - API de Acompanhamento de Treinos

API backend para acompanhamento pessoal de treinos de musculação.

**Stack**: Java 21 | Quarkus 3.8 | PostgreSQL | Arquitetura Hexagonal | TDD

---

## Pré-requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL 15+ (para dev/prod)
- Docker (opcional, para Testcontainers)

---

## Setup Rápido

```bash
# 1. Criar banco de dados
psql -U postgres -c "CREATE DATABASE gym_backend;"
psql -U postgres -c "CREATE USER gymuser WITH PASSWORD 'gympass';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE gym_backend TO gymuser;"

# 2. Rodar em modo dev (hot reload + Flyway automático)
./mvnw quarkus:dev

# 3. Verificar saúde
curl http://localhost:8080/q/health
```

---

## Testes

```bash
# Testes unitários (rápidos, sem banco)
./mvnw test

# Testes de integração (requer PostgreSQL ou Testcontainers)
./mvnw verify

# Relatório de cobertura (meta: >80% na camada de domínio)
./mvnw jacoco:report
# Relatório em: target/site/jacoco/index.html
```

---

## Endpoints Principais

### Exercícios
| Método | URL | Descrição |
|--------|-----|-----------|
| `POST` | `/api/exercicios` | Criar exercício |
| `GET` | `/api/exercicios` | Listar (paginado) |
| `GET` | `/api/exercicios/{id}` | Buscar por ID |
| `PUT` | `/api/exercicios/{id}` | Atualizar |
| `DELETE` | `/api/exercicios/{id}` | Inativar (soft delete) |

### Fichas de Treino
| Método | URL | Descrição |
|--------|-----|-----------|
| `POST` | `/api/fichas` | Criar ficha (com V1 automática) |
| `GET` | `/api/fichas/{id}` | Buscar ficha |
| `GET` | `/api/fichas/{id}/versoes` | Listar versões |
| `POST` | `/api/fichas/{id}/versoes` | Nova versão (imutável) |
| `POST` | `/api/fichas/{id}/versoes/{n}/ativar` | Ativar versão |
| `POST` | `/api/fichas/{id}/versoes/{n}/duplicar` | Duplicar versão |

### Sessões de Treino
| Método | URL | Descrição |
|--------|-----|-----------|
| `POST` | `/api/sessoes?fichaId=` | Iniciar sessão |
| `GET` | `/api/sessoes/{id}` | Buscar sessão |
| `POST` | `/api/sessoes/{id}/series` | Registrar série |
| `PUT` | `/api/sessoes/{id}/finalizar` | Finalizar |
| `PUT` | `/api/sessoes/{id}/cancelar` | Cancelar |

### Histórico & Progressão
| Método | URL | Descrição |
|--------|-----|-----------|
| `GET` | `/api/exercicios/{id}/historico` | Histórico paginado |
| `GET` | `/api/exercicios/{id}/evolucao` | Evolução de cargas |
| `GET` | `/api/exercicios/{id}/progressao` | Sugestão 8-12 RMs |
| `GET` | `/api/exercicios/{id}/melhor-carga` | Melhor carga registrada |

---

## Estrutura do Projeto

```
src/main/java/com/gymbackend/
├── domain/            # Núcleo de negócio (sem dependências externas)
│   ├── entities/      # 6 entidades de domínio
│   ├── repositories/  # Interfaces (Ports)
│   ├── usecases/      # Casos de uso
│   ├── services/      # Serviços de domínio
│   └── exceptions/    # Exceções de domínio
├── application/       # Camada de aplicação
│   └── dto/           # Data Transfer Objects
└── infrastructure/    # Adapters (implementações concretas)
    ├── api/            # REST Controllers + Exception Handler
    ├── persistence/    # JPA/Panache Repositories
    └── security/       # JWT
```

---

## Segurança (OWASP Top 10)

- **A01** - Controle de acesso verificado em todos os endpoints
- **A02** - Senhas bcrypted, JWT HS512, HTTPS obrigatório em produção
- **A03** - JPA prepared statements (sem SQL injection)
- **A06** - Dependências monitoradas
- **A07** - JWT com expiração + rate limiting
- **A09** - Logging estruturado de operações críticas

---

## Feature: Versionamento Imutável de Fichas

Decisão crítica de design: versões de fichas nunca são alteradas.

```
Ficha "Push Pull Legs"
  ├── V1 (IMUTÁVEL - exercícios originais)
  ├── V2 (IMUTÁVEL - adicionou Supino)
  └── V3 (ATIVA - modificou ordem)
```

Cada sessão registra qual versão foi usada (audit trail).

---

## Algoritmo de Progressão 8-12 RMs

```
Se TODAS as séries >= 12 reps → AUMENTAR_CARGA (+2.5kg)
Se alguma série < 8 reps → MANTER_CARGA
Se séries entre 8-12 reps → MANTER_CARGA (progredir reps)
Se sem histórico → SEM_HISTORICO
```

---

## Gerado por

Especificação: `specs/001-acompanhamento-treinos/`  
Constituição: `.specify/memory/constitution.md` v1.0.0
