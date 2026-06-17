---

description: "Task list template for feature implementation"
---

# Tarefas: [NOME DA FEATURE]

**Entrada**: Documentos de design de `/specs/[###-nome-feature]/`

**Pré-requisitos**: plan.md (requerido), spec.md (requerido para histórias de usuário), research.md, data-model.md, contracts/

**⚠️ TESTES SÃO OBRIGATÓRIOS** (Constituição v1.0.0): Não há exemplos com testes OPCIONAIS. Testes DEVEM ser implementados PRIMEIRO (TDD).

**Organização**: Tarefas são agrupadas por história de usuário para permitir implementação e teste independente de cada história.
Tarefas DEVEM estar organizadas por camada arquitetônica (Domain → Ports → Adapters).

## Formato: `[ID] [P?] [Story] Descrição`

- **[P]**: Pode rodar em paralelo (arquivos diferentes, sem dependências)
- **[Story]**: Qual história de usuário esta tarefa pertence (e.g., US1, US2, US3)
- **Testes Primeiro**: Cada grupo de tarefas DEVE ter testes escritos ANTES da implementação
- Inclua caminhos de arquivo exatos nas descrições

## Convenções de Caminho (Arquitetura Hexagonal)

```
src/main/java/com/gymbackend/
├── domain/                 # Núcleo (sem dependências externas)
│   ├── entities/          
│   ├── repositories/       # Interfaces de porta
│   ├── usecases/          
│   └── exceptions/        
├── application/            
│   ├── ports/             # Interfaces de adapters secundários
│   └── services/          
├── infrastructure/         # Implementações concretas (adapters)
│   ├── persistence/       # JPA/Hibernate
│   ├── api/               # REST controllers
│   ├── config/            
│   └── security/          # OWASP/segurança
└── resources/             

tests/
├── unit/                  # Testes unitários (domain, application)
├── integration/           # Testes de integração (API, DB)
├── contract/              # Testes de contrato (ports)
└── acceptance/            # Testes de aceitação (casos de uso)
```

<!--
  ============================================================================
  IMPORTANTE: As tarefas abaixo são TAREFAS DE AMOSTRA para ilustração apenas.

  O comando /speckit.tasks DEVE substituir estas com tarefas reais baseadas em:
  - Histórias de usuário de spec.md (com suas prioridades P1, P2, P3...)
  - Requisitos de feature de plan.md
  - Entidades de data-model.md
  - Endpoints de contracts/

  Tarefas DEVEM estar organizadas por história de usuário SO cada história pode ser:
  - Implementada independentemente
  - Testada independentemente  
  - Entregue como incremento MVP
  
  ⚠️ TESTES SÃO OBRIGATÓRIOS POR CONSTITUIÇÃO v1.0.0
  NÃO há tarefas sem testes. TDD é obrigatório.
  NÃO mantenha estas tarefas de amostra no arquivo tasks.md gerado.
  ============================================================================
-->

## Fase 1: Configuração (Infraestrutura Compartilhada)

**Propósito**: Inicialização do projeto e estrutura básica

- [ ] T001 Criar estrutura do projeto por plano de implementação (src/main/java, tests/, pom.xml ou build.gradle)
- [ ] T002 Inicializar projeto Quarkus com Java 25 e dependências PostgreSQL
- [ ] T003 [P] Configurar linting (Checkstyle) e formatting (Google Java Style)
- [ ] T004 [P] Configurar database schema e framework de migrations (Flyway ou Liquibase)
- [ ] T005 [P] Implementar framework base de segurança (autenticação, autorização)
- [ ] T006 [P] Configurar roteamento de API REST e middleware base
- [ ] T007 Criar base de modelos/entidades (domain entities) que todas as histórias dependem
- [ ] T008 Implementar infraestrutura de tratamento de erros e logging
- [ ] T009 Configurar gerenciamento de configuração (environment, properties)
- [ ] T010 [P] Configurar testes base (JUnit 5, Testcontainers para PostgreSQL)

**Checkpoint**: Fundação pronta - implementação de histórias de usuário pode agora começar em paralelo

---

## Fase 2: Requisitos de Segurança (OWASP Top 10)

**Propósito**: Implementar proteções de segurança conforme Constituição v1.0.0

- [ ] T011 [P] Implementar validação rigorosa de entrada em validation package
- [ ] T012 [P] Configurar autenticação JWT/OAuth (conforme requisitos)
- [ ] T013 [P] Implementar autorização role-based (RBAC)
- [ ] T014 [P] Criptografia de dados sensíveis (passwords, tokens)
- [ ] T015 [P] Configurar proteção CSRF e CORS
- [ ] T016 [P] Implementar logging estruturado de eventos de segurança
- [ ] T017 Configurar dependency scanning para vulnerabilidades (OWASP A06)
- [ ] T018 [P] Testes de segurança para endpoints principais

**Checkpoint**: Proteções OWASP Base implementadas e testadas

---

## Fase 3: História de Usuário 1 - [Título] (Prioridade: P1) 🎯 MVP

**Objetivo**: [Descrição breve do que esta história entrega]

**Teste Independente**: [Como verificar que esta história funciona isoladamente]

### Testes para História de Usuário 1 ⚠️ (ESCREVER TESTES PRIMEIRO - TDD)

> **IMPORTANTE**: Escreva estes testes PRIMEIRO, garanta que FALHEM antes da implementação

- [ ] T019 [P] [US1] Teste unitário para entidade [Entity1] em tests/unit/domain/entities/test_[entity1].java
- [ ] T020 [P] [US1] Teste de contrato (port) para repositório em tests/contract/test_[repository].java
- [ ] T021 [P] [US1] Teste de integração para caso de uso em tests/integration/test_[usecase].java
- [ ] T022 [US1] Teste de integração para endpoint REST em tests/integration/api/test_[endpoint].java

### Implementação para História de Usuário 1 (após testes PASSAREM)

**Camada Domain** (lógica de negócio pura):
- [ ] T023 [P] [US1] Criar entidade [Entity1] em domain/entities/[Entity1].java
- [ ] T024 [P] [US1] Criar interface de porta [RepositoryPort] em domain/repositories/[RepositoryPort].java
- [ ] T025 [P] [US1] Criar caso de uso [UseCase1] em domain/usecases/[UseCase1].java

**Camada Application** (coordinação):
- [ ] T026 [US1] Criar serviço de aplicação [AppService] em application/services/[AppService].java (depende de T023, T025)
- [ ] T027 [US1] Implementar validação de entrada (domain/validation/)

**Camada Infrastructure** (adapters):
- [ ] T028 [P] [US1] Implementar adapter de repositório JPA em infrastructure/persistence/[Entity1]JpaAdapter.java
- [ ] T029 [P] [US1] Implementar controller REST em infrastructure/api/[Resource]Controller.java
- [ ] T030 [US1] Integrar segurança (OWASP) no endpoint
- [ ] T031 [US1] Adicionar logging estruturado para operações de US1

**Checkpoint**: História de Usuário 1 está completamente funcional e testável independentemente

---

## Fase 4: História de Usuário 2 - [Título] (Prioridade: P2)

**Objetivo**: [Descrição breve do que esta história entrega]

**Teste Independente**: [Como verificar que esta história funciona isoladamente]

### Testes para História de Usuário 2 ⚠️ (ESCREVER TESTES PRIMEIRO)

- [ ] T032 [P] [US2] Teste unitário em tests/unit/domain/
- [ ] T033 [P] [US2] Teste de contrato (port) em tests/contract/
- [ ] T034 [P] [US2] Teste de integração em tests/integration/

### Implementação para História de Usuário 2

- [ ] T035 [P] [US2] Domain: Criar entidade [Entity2] em domain/entities/[Entity2].java
- [ ] T036 [P] [US2] Domain: Criar caso de uso em domain/usecases/
- [ ] T037 [US2] Application: Criar serviço de aplicação
- [ ] T038 [US2] Infrastructure: Implementar adapter de persistência
- [ ] T039 [US2] Infrastructure: Implementar controller REST
- [ ] T040 [US2] Integrar com componentes de História de Usuário 1 (se necessário)

**Checkpoint**: Histórias de Usuário 1 E 2 funcionam independentemente

---

## Fase 5: História de Usuário 3 - [Título] (Prioridade: P3)

**Objetivo**: [Descrição breve do que esta história entrega]

**Teste Independente**: [Como verificar que esta história funciona isoladamente]

### Testes para História de Usuário 3 ⚠️ (ESCREVER TESTES PRIMEIRO)

- [ ] T041 [P] [US3] Teste unitário em tests/unit/domain/
- [ ] T042 [P] [US3] Teste de contrato em tests/contract/
- [ ] T043 [P] [US3] Teste de integração em tests/integration/

### Implementação para História de Usuário 3

- [ ] T044 [P] [US3] Domain: Criar entidade em domain/entities/
- [ ] T045 [US3] Application: Criar serviço de aplicação
- [ ] T046 [US3] Infrastructure: Implementar adapters

**Checkpoint**: Todas as histórias de usuário estão independentemente funcionais

---

[Adicione mais fases de história de usuário conforme necessário, seguindo o mesmo padrão]

---

## Fase N: Polimento & Preocupações Transversais

**Purpose**: Improvements that affect multiple user stories

- [ ] TXXX [P] Documentation updates in docs/
- [ ] TXXX Code cleanup and refactoring
- [ ] TXXX Performance optimization across all stories
- [ ] TXXX [P] Additional unit tests (if requested) in tests/unit/
- [ ] TXXX Security hardening
- [ ] TXXX Run quickstart.md validation

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - May integrate with US1 but should be independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - May integrate with US1/US2 but should be independently testable

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Models within a story marked [P] can run in parallel
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together (if tests requested):
Task: "Contract test for [endpoint] in tests/contract/test_[name].py"
Task: "Integration test for [user journey] in tests/integration/test_[name].py"

# Launch all models for User Story 1 together:
Task: "Create [Entity1] model in src/models/[entity1].py"
Task: "Create [Entity2] model in src/models/[entity2].py"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
   - Developer C: User Story 3
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
