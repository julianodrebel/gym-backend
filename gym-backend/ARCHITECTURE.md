# Arquitetura - Gym Backend

## Visão Geral

O projeto adota **Arquitetura Hexagonal** (Ports & Adapters) com separação em 3 camadas:

```
┌─────────────────────────────────────┐
│           Infrastructure            │
│  ┌───────────┐  ┌─────────────────┐ │
│  │ REST API  │  │  JPA/Panache    │ │
│  │(Adapters) │  │  (Adapters)     │ │
│  └─────┬─────┘  └────────┬────────┘ │
└────────│─────────────────│──────────┘
         │                 │
┌────────▼─────────────────▼──────────┐
│           Application               │
│       DTOs + Orchestration          │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│              Domain                  │
│  ┌─────────┐  ┌──────────────────┐  │
│  │Entities │  │  Repositories    │  │
│  │         │  │  (Ports/Ifaces)  │  │
│  └─────────┘  └──────────────────┘  │
│  ┌──────────────┐  ┌─────────────┐  │
│  │  Use Cases   │  │  Services   │  │
│  └──────────────┘  └─────────────┘  │
└─────────────────────────────────────┘
```

## Pacotes

```
com.gymbackend
├── domain
│   ├── entities            # Entidades puras (sem anotações JPA)
│   │   ├── Exercicio
│   │   ├── FichaTreino
│   │   ├── VersaoFichaTreino  ← IMUTÁVEL (invariante crítica)
│   │   ├── SessaoTreino
│   │   ├── ExercicioExecutado
│   │   └── SerieExecutada
│   ├── repositories        # Interfaces (Ports de saída)
│   │   ├── ExercicioRepository
│   │   ├── FichaTreinoRepository
│   │   ├── VersaoFichaTreinoRepository
│   │   ├── SessaoTreinoRepository
│   │   └── ExercicioExecutadoRepository
│   ├── usecases            # Casos de uso (orquestração)
│   │   ├── exercicios/     # CriarExercicioUseCase, ...
│   │   ├── fichas/         # CriarFichaUseCase, CriarNovaVersaoUseCase, ...
│   │   └── sessoes/        # SessaoTreinoUseCases
│   ├── services            # Serviços de domínio
│   │   ├── ProgressaoService  ← Algoritmo 8-12 RMs
│   │   ├── EvolucaoService    ← Cálculos de evolução
│   │   └── TempoDescansoService ← Metadados cronômetro
│   └── exceptions
│       ├── EntidadeNaoEncontradaException
│       ├── VersaoImutavelException
│       └── RegraDeNegocioException
├── application
│   └── dto                 # Request/Response DTOs
│       ├── exercicios/
│       ├── fichas/
│       ├── sessoes/
│       ├── historico/
│       └── progressao/
└── infrastructure
    ├── api                 # REST Controllers (Adapters de entrada)
    │   ├── ExercicioController
    │   ├── FichaTreinoController
    │   ├── SessaoTreinoController
    │   ├── HistoricoController
    │   └── GlobalExceptionHandler
    ├── persistence         # JPA Adapters (Adapters de saída)
    │   ├── ExercicioJpa + ExercicioRepositoryImpl
    │   ├── FichaTreinoJpa + FichaTreinoRepositoryImpl
    │   ├── VersaoFichaTreinoRepositoryImpl
    │   └── SessaoTreinoJpa + repositórios
    └── security
        ├── JwtTokenService
        └── RateLimitingFilter
```

## Decisões de Design

### 1. Versionamento Imutável de Fichas
Versões de fichas **nunca** são modificadas após criação. `validarNaoModificavel()` lança `IllegalStateException` sempre que chamado. Toda sessão de treino armazena o `versaoFichaTreinoId` para audit trail completo.

### 2. Algoritmo de Progressão 8-12 RMs
```
Zona de Estímulo: 8-12 repetições
- Todas as séries >= 12 reps → AUMENTAR_CARGA (+2.5kg)
- Alguma série < 8 reps → MANTER_CARGA (consolidar técnica)
- 8-12 reps → MANTER_CARGA (progredir reps primeiro)
```

### 3. Cronômetro Client-Side
O servidor retorna `CronoAvisoCronometroDTO` com metadados:
- `tempoDescansoRecomendado`: 60 segundos padrão
- `iniciarCrono`: true → cliente deve iniciar contador
- `avisoIntervaloInsuficiente`: true se < 30s desde última série

### 4. MVP Single-User
Para MVP, `usuarioId = 00000000-0000-0000-0000-000000000001` é hardcoded nos controllers. JWT está habilitado (`@RolesAllowed("usuario")`) mas requer setup do SmallRye JWT para produção.

### 5. Soft Delete em Exercícios
Exercícios inativados não são deletados fisicamente (`deletadoEm`). O histórico de séries permanece intacto e vinculado ao exercício inativo.

## Banco de Dados

### Tabelas
| Tabela | Descrição |
|--------|-----------|
| `exercicios` | Exercícios cadastrados (soft delete) |
| `fichas_treino` | Fichas do usuário |
| `versoes_ficha_treino` | Versões imutáveis das fichas |
| `versao_exercicio` | M:N exercícios ↔ versões |
| `sessoes_treino` | Sessões realizadas (audit trail) |
| `exercicios_executados` | Exercícios dentro da sessão |
| `series_executadas` | Séries de cada exercício executado |
| `refresh_tokens` | JWT refresh tokens (hashed) |

### Constraint Crítica (Imutabilidade)
```sql
CREATE UNIQUE INDEX uq_versao_numero_ativo
  ON versoes_ficha_treino (ficha_treino_id, numero)
  WHERE deletado_em IS NULL;
```

## Performance

Metas SLA:
- Listagens/buscas: `< 200ms` (p95)
- Histórico completo: `< 500ms`
- Cálculo de progressão: `< 100ms`

Índices criados:
- `idx_sessao_usuario` — buscas de sessões por usuário
- `idx_historico_exercicio` — histórico de séries por exercício

## Segurança

- JWT RS256 via SmallRye JWT (tokens: 15min access, 7 dias refresh)
- Bean Validation em todos os DTOs de entrada
- Respostas de erro não expõem stack traces (OWASP A09)
- SQL via JPA/Panache (sem concatenação de string)
- Rate limiting por endpoint de autenticação
