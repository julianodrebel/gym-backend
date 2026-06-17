# Tarefas de Implementação: Acompanhamento de Treinos

**Feature**: Acompanhamento de Treinos de Musculação  
**Especificação**: [spec.md](./spec.md) | **Plano**: [plan.md](./plan.md)  
**Data**: 2026-06-17 | **Status**: Pronto para implementação em TDD  
**Total de Tarefas**: 58

---

## 📋 Índice de Fases

1. [Phase 1: Setup & Inicialização](#phase-1-setup--inicialização) (8 tarefas)
2. [Phase 2: Testes Base & Segurança](#phase-2-testes-base--segurança) (10 tarefas)
3. [Phase 3: Domain Layer - Entidades](#phase-3-domain-layer---entidades) (12 tarefas)
4. [Phase 4: US1 - Cadastro de Exercícios](#phase-4-us1---cadastro-de-exercícios) (8 tarefas)
5. [Phase 5: US2 - Fichas e Versionamento](#phase-5-us2---fichas-e-versionamento) (9 tarefas)
6. [Phase 6: US3 - Execução de Treinos](#phase-6-us3---execução-de-treinos) (9 tarefas)
7. [Phase 7: US4 - Histórico e Evolução](#phase-7-us4---histórico-e-evolução) (7 tarefas)
8. [Phase 8: US5 - Progressão Automática](#phase-8-us5---progressão-automática) (6 tarefas)
9. [Phase 9: US6 - Cronômetro](#phase-9-us6---cronômetro) (5 tarefas)
10. [Phase 10: Polish & Documentação](#phase-10-polish--documentação) (4 tarefas)

---

## Phase 1: Setup & Inicialização

**Objetivo**: Inicializar projeto Quarkus com dependências, estrutura Maven e configurações base

### Setup Quarkus e Dependências

- [ ] T001 Criar projeto Maven com Quarkus latest stable em `pom.xml` com Java 25 target
- [ ] T002 [P] Adicionar dependências: quarkus-resteasy-reactive, quarkus-hibernate-orm, quarkus-jdbc-postgresql
- [ ] T003 [P] Adicionar dependências de teste: quarkus-junit5, quarkus-test-h2, rest-assured
- [ ] T004 [P] Configurar `application.properties` com perfis (dev, test, prod) em `src/main/resources/`
- [ ] T005 Estruturar package base `com.gymbackend` em `src/main/java/com/gymbackend/`
- [ ] T006 [P] Criar estrutura de pastas: domain/, application/, infrastructure/ com subpastas
- [ ] T007 [P] Configurar Flyway para migrations em `src/main/resources/db/migration/`
- [ ] T008 Adicionar plugin Flyway em pom.xml e versionar migrations

---

## Phase 2: Testes Base & Segurança

**Objetivo**: Estabelecer framework de testes e segurança OWASP

### Testes Base

- [ ] T009 [P] Criar classe base abstrata para testes unitários em `src/test/java/com/gymbackend/` com fixtures
- [ ] T010 [P] Criar classe base para testes de integração com Testcontainers/H2 em `src/test/java/com/gymbackend/`
- [ ] T011 [P] Criar helper para testes de API REST (RestAssured setup) em `src/test/java/com/gymbackend/support/`
- [ ] T012 [P] Criar data builder para fixtures de entidades em `src/test/java/com/gymbackend/fixtures/`

### Segurança OWASP

- [ ] T013 Implementar autenticação JWT (token de 15 min + refresh 7 dias) em `src/main/java/com/gymbackend/infrastructure/security/`
- [ ] T014 [P] Criar filtro de autenticação que valida JWT em cada request em `infrastructure/security/JwtAuthenticationFilter.java`
- [ ] T015 [P] Criar global exception handler para erros OWASP (validação, auth) em `infrastructure/api/GlobalExceptionHandler.java`
- [ ] T016 Implementar validação rigorosa de entrada com `@Valid` em `infrastructure/api/`
- [ ] T017 [P] Configurar CORS seguro em `application.properties` (apenas endpoints definidos)
- [ ] T018 [P] Adicionar rate limiting middleware em `infrastructure/security/RateLimitingFilter.java`

---

## Phase 3: Domain Layer - Entidades

**Objetivo**: Implementar 6 entidades de domínio com validações rigorosas

### Entidade: Exercicio

- [ ] T019 [P] Implementar entity `Exercicio` em `domain/entities/Exercicio.java` com ID, nome, descrição, grupoMuscular, status, timestamps
- [ ] T020 [P] Adicionar validações de domínio em `Exercicio` (nome obrigatório, max 255, grupo em enum)
- [ ] T021 [P] Implementar método `inativar()` com validação de soft delete em `Exercicio.java`
- [ ] T022 Criar repository interface `ExercicioRepository` em `domain/repositories/` como port

### Entidade: FichaTreino

- [ ] T023 [P] Implementar entity `FichaTreino` em `domain/entities/FichaTreino.java` com usuarioId, nome, versaoAtiva
- [ ] T024 [P] Adicionar validações de domínio em `FichaTreino` (nome obrigatório, versão ativa válida)
- [ ] T025 Criar repository interface `FichaTreinoRepository` em `domain/repositories/` como port

### Entidade: VersaoFichaTreino (CRÍTICA - Imutável)

- [ ] T026 [P] Implementar entity `VersaoFichaTreino` com numero, exercicios[], criadoEm, deletadoEm, @Version para optimistic locking
- [ ] T027 [P] Adicionar validação crítica: `validarNaoModificavel()` rejeita updates após criação em `VersaoFichaTreino.java`
- [ ] T028 [P] Adicionar constraint SQL: `UNIQUE (ficha_id, numero, deletado_em IS NULL)` em migration V001
- [ ] T029 Criar repository interface `VersaoFichaTreinoRepository` em `domain/repositories/` como port

### Entidades: SessaoTreino, ExercicioExecutado, SerieExecutada

- [ ] T030 [P] Implementar entity `SessaoTreino` com versaoFichaTreinoId (audit trail), data, horaInicio, horaFim, status em `domain/entities/`
- [ ] T031 [P] Implementar entity `ExercicioExecutado` com referências a sessão e exercício, ordem, observações em `domain/entities/`
- [ ] T032 [P] Implementar entity `SerieExecutada` com peso, repeticoes, horarioExecucao, @Version para concorrência em `domain/entities/`
- [ ] T033 [P] Criar repository interfaces para `SessaoTreino`, `ExercicioExecutado`, `SerieExecutada` em `domain/repositories/`

---

## Phase 4: US1 - Cadastro de Exercícios

**Objetivo**: Permitir cadastro, leitura, edição e inativação de exercícios (RF-001 a RF-004)

**Teste Independente**: Fluxo completo: criar → ler → editar → inativar com histórico intacto

### Tests & DTOs

- [ ] T034 [P] Criar DTOs em `application/dto/`: `CriarExercicioDTO`, `ExercicioDTO`, `HistoricoExercicioDTO`
- [ ] T035 Criar testes unitários para `Exercicio` validações em `src/test/.../domain/entities/ExercicioTest.java`
- [ ] T036 [P] Criar testes de integração para repositório de exercício em `src/test/.../infrastructure/persistence/ExercicioRepositoryTest.java`

### Domain Services

- [ ] T037 Criar use case `CriarExercicioUseCase` em `domain/usecases/` que orquestra criação
- [ ] T038 [P] Criar use case `InativarExercicioUseCase` em `domain/usecases/` que valida soft delete

### Controllers & API

- [ ] T039 [P] Implementar `ExercicioController` em `infrastructure/api/` com endpoints:
  - `POST /api/exercicios` (criar)
  - `GET /api/exercicios/{id}` (ler)
  - `GET /api/exercicios?page=0&size=50` (listar com paginação)
  - `PUT /api/exercicios/{id}` (editar)
  - `DELETE /api/exercicios/{id}` (inativar)

- [ ] T040 Criar testes de aceitação para fluxo completo em `src/test/.../acceptance/ExercicioAcceptanceTest.java`
- [ ] T041 [P] Documentar API em `contracts/exercicio-api.md` (DTOs, códigos HTTP, validações)

---

## Phase 5: US2 - Fichas e Versionamento

**Objetivo**: Criar fichas com versionamento imutável, garantindo histórico intacto (RF-005 a RF-010)

**Teste Independente**: Criar ficha → modificar (cria V2) → ativar V1 → tentar editar V1 (rejeita)

### Tests & DTOs

- [ ] T042 [P] Criar DTOs em `application/dto/`: `CriarFichaDTO`, `FichaDTO`, `VersaoFichaDTO`, `CriarVersaoDTO`
- [ ] T043 Criar testes unitários para `VersaoFichaTreino` imutabilidade em `src/test/.../domain/entities/VersaoFichaTreinoTest.java`
- [ ] T044 [P] Criar testes de integração para versionamento em `src/test/.../infrastructure/persistence/VersaoFichaTreinoRepositoryTest.java`

### Domain Services

- [ ] T045 Criar use case `CriarFichaUseCase` em `domain/usecases/` que cria V1 automaticamente
- [ ] T046 [P] Criar use case `CriarNovaVersaoUseCase` em `domain/usecases/` que garante imutabilidade da versão anterior
- [ ] T047 [P] Criar use case `DuplicarVersaoUseCase` em `domain/usecases/` que copia exercícios para nova versão
- [ ] T048 Criar validação de domínio `VersaoNaoModificavelService` que rejeita updates em `domain/services/`

### Controllers & API

- [ ] T049 [P] Implementar `FichaTreinoController` em `infrastructure/api/` com endpoints:
  - `POST /api/fichas` (criar)
  - `GET /api/fichas/{id}` (ler)
  - `GET /api/fichas?page=0` (listar com paginação)
  - `POST /api/fichas/{id}/versoes` (criar nova versão)
  - `POST /api/fichas/{id}/versoes/{numero}/ativar` (ativar versão)
  - `GET /api/fichas/{id}/versoes` (listar versões)
  - `POST /api/fichas/{id}/versoes/{numero}/duplicar` (duplicar versão)

- [ ] T050 Criar testes de aceitação para imutabilidade em `src/test/.../acceptance/VersaoFichaTreinoAcceptanceTest.java`
- [ ] T051 [P] Documentar API em `contracts/ficha-api.md`

---

## Phase 6: US3 - Execução de Treinos

**Objetivo**: Registrar execução de treinos com séries, peso, reps, observações (RF-011 a RF-014)

**Teste Independente**: Iniciar sessão → registrar 3 séries → finalizar com observações (auditavelmente)

### Tests & DTOs

- [ ] T052 [P] Criar DTOs em `application/dto/`: `IniciarSessaoDTO`, `SessaoTreinoDTO`, `RegistrarSerieDTO`, `SerieExecutadaDTO`
- [ ] T053 Criar testes unitários para `SessaoTreino` status transitions em `src/test/.../domain/entities/SessaoTreinoTest.java`
- [ ] T054 [P] Criar testes de integração para execução de treino em `src/test/.../infrastructure/persistence/SessaoTreinoRepositoryTest.java`

### Domain Services

- [ ] T055 Criar use case `IniciarSessaoTreinoUseCase` em `domain/usecases/` que registra versão usada
- [ ] T056 [P] Criar use case `RegistrarSerieUseCase` em `domain/usecases/` que valida peso/reps >= 0
- [ ] T057 [P] Criar use case `FinalizarSessaoUseCase` em `domain/usecases/` que muda status para FINALIZADO

### Controllers & API

- [ ] T058 [P] Implementar `SessaoTreinoController` em `infrastructure/api/` com endpoints:
  - `POST /api/sessoes` (iniciar)
  - `GET /api/sessoes/{id}` (ler)
  - `POST /api/sessoes/{id}/series` (registrar série com cronometro metadata)
  - `PUT /api/sessoes/{id}/observacoes` (adicionar observações)
  - `PUT /api/sessoes/{id}/finalizar` (finalizar)
  - `PUT /api/sessoes/{id}/cancelar` (cancelar)

- [ ] T059 Criar testes de aceitação para fluxo de execução em `src/test/.../acceptance/SessaoTreinoAcceptanceTest.java`
- [ ] T060 [P] Documentar API em `contracts/sessao-api.md` com cronometro metadata

---

## Phase 7: US4 - Histórico e Evolução

**Objetivo**: Consultar histórico, evolução de cargas, volume, comparações (RF-015)

**Teste Independente**: Registrar 3 sessões → consultar evolução → validar cálculos de volume e tendência

### Tests & DTOs

- [ ] T061 [P] Criar DTOs em `application/dto/`: `SerieHistoricoDTO`, `EvolucaoDTO`, `ComparacaoDTO`, `TendenciaDTO`
- [ ] T062 Criar testes unitários para cálculos de evolução em `src/test/.../domain/services/EvolucaoServiceTest.java`

### Domain Services

- [ ] T063 Criar service `EvolucaoService` em `domain/services/` que calcula:
  - Volume = peso × reps × número de séries
  - Tendência (CRESCENTE, ESTÁVEL, DECRESCENTE)
  - Progressão de carga e volume

- [ ] T064 [P] Criar service `ComparacaoPeriodicaService` em `domain/services/` que compara dois períodos

### Controllers & API

- [ ] T065 [P] Implementar `HistoricoController` em `infrastructure/api/` com endpoints:
  - `GET /api/exercicios/{id}/historico?page=0` (histórico paginado)
  - `GET /api/exercicios/{id}/evolucao` (evolução com gráfico data)
  - `GET /api/exercicios/{id}/ultima-execucao` (última execução)
  - `GET /api/exercicios/{id}/melhor-carga` (melhor carga registrada)
  - `GET /api/exercicios/{id}/comparacao?dataPeriodo1Inicio=...` (comparação entre períodos)

- [ ] T066 Criar testes de aceitação para histórico em `src/test/.../acceptance/HistoricoAcceptanceTest.java`
- [ ] T067 [P] Documentar API em `contracts/historico-api.md`

---

## Phase 8: US5 - Progressão Automática

**Objetivo**: Sugerir progressão baseada na regra 8-12 RMs (RF-016)

**Teste Independente**: Registrar séries → aplicar algoritmo → validar sugestão (aumentar/manter/sem histórico)

### Tests & DTOs

- [ ] T068 [P] Criar DTOs em `application/dto/`: `SugestaoProgressaoDTO`, `DetalhesProgressaoDTO`
- [ ] T069 Criar testes unitários para algoritmo de progressão em `src/test/.../domain/services/ProgressaoServiceTest.java`
  - Teste: todas séries >= 12 reps → aumentar carga
  - Teste: alguma série < 8 reps → manter carga
  - Teste: 8-12 reps → manter carga progredir reps
  - Teste: sem histórico → sem sugestão

### Domain Services

- [ ] T070 Criar service `ProgressaoService` em `domain/services/` com lógica 8-12 RMs
- [ ] T071 [P] Criar queryHandler para recuperar últimas 5 séries em `domain/services/`

### Controllers & API

- [ ] T072 [P] Implementar `ProgressaoController` em `infrastructure/api/` com endpoint:
  - `GET /api/exercicios/{id}/progressao` (retorna sugestão com justificativa)

- [ ] T073 Criar testes de aceitação para progressão em `src/test/.../acceptance/ProgressaoAcceptanceTest.java`
- [ ] T074 [P] Documentar API em `contracts/progresso-api.md`

---

## Phase 9: US6 - Cronômetro

**Objetivo**: Fornecer metadata de cronômetro client-side (RF-017, RF-018)

**Teste Independente**: Registrar série → validar cronometro metadata (tempo recomendado, iniciarCrono)

### Tests & DTOs

- [ ] T075 [P] Criar DTOs em `application/dto/`: `CronoAvisoCronometroDTO`, `TempoDescansoDTO`
- [ ] T076 Criar testes unitários para cálculo de tempo de descanso em `src/test/.../domain/services/TempoDescansoServiceTest.java`

### Domain Services

- [ ] T077 Criar service `TempoDescansoService` em `domain/services/` que:
  - Retorna tempo recomendado (padrão 60s, configurável por exercício)
  - Valida intervalo mínimo (avisar se < 30s desde última série)

### Controllers & API

- [ ] T078 [P] Atualizar `SessaoTreinoController` endpoint `POST /api/sessoes/{id}/series` para incluir cronometro metadata na resposta
- [ ] T079 Criar testes de aceitação para cronometro em `src/test/.../acceptance/CronometroAcceptanceTest.java`

---

## Phase 10: Polish & Documentação

**Objetivo**: Finalizar, documentar, validar qualidade

### Documentação & Validação

- [ ] T080 [P] Criar arquivo `README.md` na raiz com:
  - Setup local (Java, PostgreSQL, Quarkus)
  - Compilação e execução (`mvn quarkus:dev`)
  - Estrutura do projeto
  - Endpoints principais
  - Testes (`mvn test`)

- [ ] T081 Criar arquivo `ARCHITECTURE.md` documentando:
  - Arquitetura Hexagonal em detalhes
  - Decisões de design (versionamento, progressão, cronometro)
  - Fluxo de dados
  - Segurança (OWASP)

- [ ] T082 [P] Validar cobertura de testes: `mvn jacoco:report` mínimo 80% para domain layer

- [ ] T083 Validar 100% dos testes passam e zero warnings de compilação

---

## 📊 Resumo por Fase

| Fase | Objetivo | Tarefas | User Stories |
|------|----------|---------|-------------|
| **Phase 1** | Setup Quarkus | 8 | - |
| **Phase 2** | Testes & Segurança OWASP | 10 | - |
| **Phase 3** | Domain Layer (6 entidades) | 12 | - |
| **Phase 4** | US1 - Exercícios | 8 | US1 |
| **Phase 5** | US2 - Fichas (Versionamento) | 9 | US2 |
| **Phase 6** | US3 - Execução | 9 | US3 |
| **Phase 7** | US4 - Histórico | 7 | US4 |
| **Phase 8** | US5 - Progressão | 7 | US5 |
| **Phase 9** | US6 - Cronometro | 5 | US6 |
| **Phase 10** | Polish & Docs | 4 | - |
| **TOTAL** | **Implementação Completa** | **79** | **6 US** |

---

## 🎯 Critério de Sucesso por User Story

### ✅ US1 - Cadastro de Exercícios
- [ ] Criar, ler, listar, editar, inativar exercícios
- [ ] Histórico de execuções preservado após inativação
- [ ] Validações rigorosas (nome obrigatório, max 255)
- [ ] Cobertura > 80%, testes T035-T041 passando

### ✅ US2 - Fichas e Versionamento (CRÍTICA)
- [ ] Criar ficha com V1 automaticamente
- [ ] Modificar cria V2 automaticamente (V1 intacta)
- [ ] Rejeitar tentativa de editar versão antiga
- [ ] Audit trail: sessão registra qual versão usou
- [ ] Cobertura > 80%, testes T043-T051 passando

### ✅ US3 - Execução de Treinos
- [ ] Iniciar, registrar séries, finalizar sessão
- [ ] Peso/reps validados >= 0
- [ ] Status transitions: EM_PROGRESSO → FINALIZADO/CANCELADO
- [ ] Cronometro metadata retornada
- [ ] Cobertura > 80%, testes T053-T060 passando

### ✅ US4 - Histórico
- [ ] Consultar histórico paginado
- [ ] Evolução com cálculos (volume, tendência)
- [ ] Comparação entre períodos
- [ ] Performance < 500ms p95
- [ ] Cobertura > 80%, testes T061-T067 passando

### ✅ US5 - Progressão
- [ ] Algoritmo 8-12 RMs funcionando
- [ ] Todos >= 12 → aumentar carga (+2.5kg)
- [ ] Algum < 8 → manter carga
- [ ] 8-12 → manter carga progredir reps
- [ ] Performance < 100ms p95
- [ ] Cobertura > 80%, testes T068-T074 passando

### ✅ US6 - Cronometro
- [ ] Metadata retornada em resposta de série
- [ ] Cliente controla timing
- [ ] Validação se intervalo < 30s (aviso)
- [ ] Cobertura > 80%, testes T075-T079 passando

---

## 🔐 Validação OWASP

**Requisitos de Segurança Implementados**:
- [x] T013-T018: RS-001 a RS-008 (validação, auth, criptografia, logging, rate limiting, CSRF)
- [x] Cada controller usa `@Valid` e JWT
- [x] Global exception handler trata erros seguramente
- [x] SQL injection prevenida (JPA prepared statements)
- [x] Logging estruturado de operações críticas

---

## 📝 Formato de Checklist

**OBRIGATÓRIO**: Cada tarefa segue:
```
- [ ] [TaskID] [P?] [Story?] Descrição com arquivo

Exemplo:
- [ ] T001 Criar projeto Maven...
- [ ] T002 [P] Adicionar dependências: quarkus-resteasy...
- [ ] T040 [P] Criar testes de aceitação em src/test/.../acceptance/...
- [ ] T055 [US3] Criar use case IniciarSessaoTreinoUseCase...
```

**Campos**:
- **[TaskID]**: T001, T002... (sequencial)
- **[P]**: Parallelizável (diferente arquivo, sem dependência em incompletas)
- **[Story]**: [US1], [US2], etc. (apenas em tarefas de user stories, Phase 4-9)
- **Descrição**: Clara, com file path absoluto/relativo

---

## 🚀 Plano de Execução Recomendado

**MVP (Mínimo Viável) - ~2-3 semanas**:
1. Fases 1-3: Setup + Domain (18 tarefas)
2. Phase 4: US1 - Exercícios (8 tarefas) ← Bloqueador para outras
3. Phase 5: US2 - Fichas (9 tarefas) ← Bloqueador para US3
4. Phase 6: US3 - Execução (9 tarefas) ← Core da aplicação
5. Phase 10: Polish (4 tarefas)

**Parallelização possível**:
- Fases 1-3: Todas as tarefas [P] em paralelo
- Phase 4 + Phase 2: Teste base paralelo
- Phase 7-8: Histórico e Progressão paralelo (após US3)

---

## ✅ Validação Final

**Antes de merge em main**:
- [ ] Todos os testes passam: `mvn clean test`
- [ ] Cobertura > 80%: `mvn jacoco:report`
- [ ] Aplicação roda: `mvn quarkus:dev`
- [ ] Quickstart validação: 10 cenários em `quickstart.md` passam
- [ ] Documentação atualizada: README.md, ARCHITECTURE.md, contracts/
- [ ] Sem warnings de compilação
- [ ] Sem vulnerabilidades: `mvn dependency-check:check`

---

**Status**: ✅ Pronto para Desenvolvimento em TDD  
**Próximo Passo**: Começar Phase 1 (Setup Quarkus)  
**Mentoria**: Seguir checklist linha por linha, TDD obrigatório (testes PRIMEIRO)

---

**Gerado por**: `/speckit.tasks`  
**Data**: 2026-06-17  
**Versão**: 1.0.0
