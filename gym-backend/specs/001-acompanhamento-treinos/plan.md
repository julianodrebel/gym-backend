# Plano de Implementação: Acompanhamento de Treinos de Musculação

**Branch**: `001-acompanhamento-treinos` | **Data**: 2026-06-17 | **Spec**: [spec.md](spec.md)

**Entrada**: Feature specification from `specs/001-acompanhamento-treinos/spec.md`

**Status**: Fase 0-1 - Pesquisa e Design

---

## Resumo

Sistema backend para acompanhamento pessoal de treinos de musculação. Permite cadastro de exercícios, organização em fichas de treino (com versionamento imutável), registro de sessões de treino, histórico detalhado de desempenho, e sugestão automática de progressão de carga baseada em faixa-alvo (8-12 reps).

**Abordagem Técnica**:
- Arquitetura Hexagonal com 3 camadas: Domain (lógica de negócio), Application (casos de uso), Infrastructure (adapters)
- Domain Model centrado em 6 entidades: Exercício, Ficha, Versão da Ficha, Sessão, Exercício Executado, Série
- Imutabilidade de versões de fichas como requisito crítico
- Segurança OWASP integrada (autenticação JWT, validação rigorosa, logging)
- TDD obrigatório: testes escritos antes da implementação

---

## Contexto Técnico

**Linguagem/Versão**: Java 25 (preview features apenas com justificativa explícita)

**Framework**: Quarkus (versão estável mais recente compatível com Java 25)

**Storage**: PostgreSQL (banco de dados relacional obrigatório)

**Testing**: JUnit 5, Testcontainers, RestAssured

**Architecture**: Hexagonal Architecture (Ports & Adapters) - OBRIGATÓRIO

**Target Platform**: Backend service (servidor Linux/container)

**Project Type**: Microserviço / REST API com lógica de negócio

**Performance Goals**: <200ms p95 para operações de consulta; <500ms para cálculos de progresso

**Constraints**: Imutabilidade de versões CRÍTICA; audit trail completo

**Scale/Scope**: MVP para 1 usuário; arquitetura preparada para expansão multi-tenant

### Estrutura de Camadas Hexagonal

```
src/main/java/com/gymbackend/
├── domain/                          # Núcleo de negócio (sem dependências externas)
│   ├── entities/
│   │   ├── Exercicio.java
│   │   ├── FichaTreino.java
│   │   ├── VersaoFichaTreino.java
│   │   ├── SessaoTreino.java
│   │   ├── ExercicioExecutado.java
│   │   └── SerieExecutada.java
│   ├── repositories/                # Interfaces (Ports)
│   ├── usecases/                    # Casos de uso do domínio
│   ├── services/                    # Serviços de domínio
│   └── exceptions/                  # Exceções de domínio
│
├── application/                     # Camada de aplicação
│   ├── dto/                         # Data Transfer Objects
│   ├── ports/                       # Interfaces de Adapters Secundários
│   └── services/                    # Serviços de aplicação (orquestração)
│
├── infrastructure/                  # Adapters (implementações concretas)
│   ├── persistence/                 # JPA/Hibernate
│   ├── api/                         # REST controllers
│   ├── config/                      # Configuração Quarkus
│   ├── security/                    # JWT, CORS, rate limiting
│   └── migration/                   # Flyway migrations
│
└── resources/
    ├── application.properties
    └── db/migration/

tests/
├── unit/                            # Testes unitários
├── integration/                     # Testes de integração
├── contract/                        # Testes de contrato (ports)
└── acceptance/                      # Testes de aceitação
```

---

## Verificação de Conformidade com Constituição

*GATE: Deve passar antes de Fase 0 Pesquisa. Re-validar após Fase 1 Design.*

**Verificação Obrigatória** (Constituição v1.0.0):

- [x] **Arquitetura Hexagonal (Ports & Adapters) será aplicada?**
  - ✅ SIM. Domain, Application, Infrastructure layers claramente separadas. Estrutura definida acima.

- [x] **Stack fixo será respeitado? (Java 25, Quarkus, PostgreSQL)**
  - ✅ SIM. Java 25, Quarkus latest stable, PostgreSQL confirmados. Nenhuma alteração proposta.

- [x] **Testes estão planejados para cada caso de uso?**
  - ✅ SIM. 7 casos de uso com testes unitários, integração e aceitação. TDD obrigatório em Phase 2.

- [x] **Complexidade será justificada (YAGNI)?**
  - ✅ SIM. MVP com 20 requisitos. Versionamento e imutabilidade são core requirements.

- [x] **Requisitos de segurança (OWASP Top 10) estão identificados?**
  - ✅ SIM. 8 requisitos OWASP. Será detalhado em Phase 1.

- [x] **Todo conteúdo será redigido em pt-BR?**
  - ✅ SIM. 100% português brasileiro.

**Status**: ✅ TODOS OS GATES PASSAM - Proceder para Fase 0-1.

---

## Estrutura de Projeto

### Documentação (esta feature)

```
specs/001-acompanhamento-treinos/
├── spec.md                          # Feature specification ✅
├── plan.md                          # Este arquivo (saída de /speckit.plan)
├── research.md                      # Fase 0 output ⏳
├── data-model.md                    # Fase 1 output ⏳
├── quickstart.md                    # Fase 1 output ⏳
├── contracts/                       # Fase 1 output ⏳
│   ├── exercicio-api.md
│   ├── ficha-api.md
│   ├── sessao-api.md
│   ├── progresso-api.md
│   └── historico-api.md
└── checklists/
    └── requirements.md              # ✅ Já validado
```

### Código-fonte (raiz do repositório)

Estrutura Maven single-project com Quarkus.

**Decisão de Estrutura**: Single project Maven/Gradle. Estrutura Hexagonal com 3 camadas. PostgreSQL como repositório central.

---

## Rastreamento de Complexidade

| Aspecto | Justificativa | Alternativa Rejeitada |
|---------|---------------|----------------------|
| Versionamento Imutável | Requisito core (RF-007) | Não há alternativa simples |
| Audit Trail Versão→Sessão | Requisito core (RF-009) | Não há alternativa simples |
| Camada Application Services | Orquestração de 7 use cases | Direct domain logic em controllers (viola Arquitetura Hexagonal) |
| 6 Entidades de Domínio | Cada uma com responsabilidade clara | Consolidar (viola single responsibility) |

---

## Fase 0: Pesquisa e Clarificação

**Unknowns Identificados**:

1. ❌ Performance Goals: Nenhuma métrica específica definida
   - **Solução**: Definir em research.md baseado em MVP

2. ❌ Constraints: Nenhuma restrição específica além de imutabilidade
   - **Solução**: Definir baseado em MVP

3. ❌ Padrão de Versionamento em JPA
   - **Pesquisa**: Best practices Quarkus/Hibernate para immutability

4. ❌ Implementação de Progressão Automática
   - **Pesquisa**: Fórmulas e algoritmos de progressão de força

5. ❌ Cronômetro em Backend
   - **Pesquisa**: Cliente vs servidor-side timing

6. ❌ JWT + Single User
   - **Pesquisa**: Padrões de token refresh

**Output Esperado**: research.md com decisões documentadas

---

## Fase 1: Design e Contratos

**Entregas**:

### 1. Data Model (`data-model.md`)
- 6 Entidades com relacionamentos
- Atributos, tipos, constraints
- Regras de validação
- Estados e transições

### 2. Interface Contracts (`contracts/`)
- 5 Contratos REST
- Endpoints (GET, POST, PUT, DELETE)
- DTOs de requisição/resposta
- Códigos HTTP

### 3. Guia de Validação (`quickstart.md`)
- Cenários end-to-end
- Comandos de validação
- Resultados esperados

### 4. Atualização de Contexto de Agent
- Referenciar este plano em `.github/copilot-instructions.md`

---

## Fase 2: Tarefas de Implementação

Será gerada por `/speckit.tasks` com:
- Setup de projeto Quarkus
- Testes base
- Implementação de cada caso de uso em TDD
- Integração de segurança OWASP
- ~50-60 tarefas organizadas por fase

---

## Status e Próximas Ações

**Status Atual**: Fase 0-1 em andamento

**Próximas Ações**:
1. ⏳ Gerar research.md
2. ⏳ Gerar data-model.md
3. ⏳ Gerar contracts/*.md
4. ⏳ Gerar quickstart.md
5. ⏭️ Executar `/speckit.tasks` para tarefas de implementação

---

**Versão**: 1.0.0 | **Data de Criação**: 2026-06-17
