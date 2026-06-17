# Implementation Plan: [FEATURE]

**Branch**: `[###-feature-name]` | **Date**: [DATE] | **Spec**: [link]

**Input**: Feature specification from `/specs/[###-feature-name]/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

[Extract from feature spec: primary requirement + technical approach from research]

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. For gym-backend, the following are FIXED per Constitution v1.0.0.
-->

**Language/Version**: Java 25 (preview features apenas com justificativa explícita)

**Framework**: Quarkus (versão estável mais recente compatível com Java 25)

**Storage**: PostgreSQL (banco de dados relacional obrigatório)

**Testing**: JUnit 5, Testcontainers, Arquillian (ou padrão Quarkus recomendado)

**Architecture**: Hexagonal Architecture (Ports & Adapters) - OBRIGATÓRIO

**Target Platform**: Backend service (servidor Linux/container)

**Project Type**: Microserviço / REST API com lógica de negócio

**Performance Goals**: [NEEDS CLARIFICATION - domain-specific, e.g., 1000 req/s]

**Constraints**: [NEEDS CLARIFICATION - domain-specific, e.g., <200ms p95]

**Scale/Scope**: [NEEDS CLARIFICATION - domain-specific]

**Estrutura de Camadas Hexagonal** (OBRIGATÓRIA):
```
src/main/java/com/gymbackend/
├── domain/                 # Núcleo de negócio (sem dependências externas)
│   ├── entities/           # Modelos de domínio
│   ├── repositories/       # Interfaces (portas) de repositório
│   ├── usecases/           # Casos de uso
│   └── exceptions/         # Exceções de domínio
├── application/            # Camada de aplicação
│   ├── ports/              # Interfaces (adapters secundários)
│   └── services/           # Serviços de aplicação
├── infrastructure/         # Adapters
│   ├── persistence/        # JPA/Hibernate (adapters de repositório)
│   ├── api/                # REST controllers (adapters primários)
│   ├── config/             # Configuração Quarkus
│   └── security/           # Segurança/OWASP
└── resources/              # application.properties, etc.
```

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

**Verificação Obrigatória** (Constituição v1.0.0):
- [ ] Arquitetura Hexagonal (Ports & Adapters) será aplicada?
- [ ] Stack fixo será respeitado? (Java 25, Quarkus, PostgreSQL)
- [ ] Testes estão planejados para cada caso de uso?
- [ ] Complexidade será justificada (YAGNI)?
- [ ] Requisitos de segurança (OWASP Top 10) estão identificados?
- [ ] Todo conteúdo será redigido em pt-BR?

Violações à constituição DEVEM ser documentadas na seção "Complexity Tracking" abaixo.

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
# [REMOVE IF UNUSED] Option 1: Single project (DEFAULT)
src/
├── models/
├── services/
├── cli/
└── lib/

tests/
├── contract/
├── integration/
└── unit/

# [REMOVE IF UNUSED] Option 2: Web application (when "frontend" + "backend" detected)
backend/
├── src/
│   ├── models/
│   ├── services/
│   └── api/
└── tests/

frontend/
├── src/
│   ├── components/
│   ├── pages/
│   └── services/
└── tests/

# [REMOVE IF UNUSED] Option 3: Mobile + API (when "iOS/Android" detected)
api/
└── [same as backend above]

ios/ or android/
└── [platform-specific structure: feature modules, UI flows, platform tests]
```

**Structure Decision**: [Document the selected structure and reference the real
directories captured above]

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
