# Especificação de Feature: [NOME DA FEATURE]

**Branch de Feature**: `[###-nome-feature]`

**Criada**: [DATA]

**Status**: Rascunho

**Entrada**: Descrição do usuário: "$ARGUMENTS"

## Cenários de Usuário e Testes *(obrigatório)*

<!--
  IMPORTANTE: Histórias de usuário DEVEM ser PRIORIZADAS como jornadas do usuário ordenadas por importância.
  Cada história de usuário/jornada DEVE ser INDEPENDENTEMENTE TESTÁVEL - significando que se você implementar
  apenas UMA delas, você ainda deve ter um MVP (Produto Mínimo Viável) que entregue valor.

  Atribua prioridades (P1, P2, P3, etc.) a cada história, onde P1 é a mais crítica.
  Pense em cada história como uma fatia independente de funcionalidade que pode ser:
  - Desenvolvida independentemente
  - Testada independentemente
  - Deployada independentemente
  - Demonstrada aos usuários independentemente

  ⚠️ LEMBRE-SE: Constituição v1.0.0 determina que TESTES SÃO OBRIGATÓRIOS.
  Nenhum caso de uso é entregue sem seus testes.
-->

### User Story 1 - [Brief Title] (Priority: P1)

[Describe this user journey in plain language]

**Why this priority**: [Explain the value and why it has this priority level]

**Independent Test**: [Describe how this can be tested independently - e.g., "Can be fully tested by [specific action] and delivers [specific value]"]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]
2. **Given** [initial state], **When** [action], **Then** [expected outcome]

---

### User Story 2 - [Brief Title] (Priority: P2)

[Describe this user journey in plain language]

**Why this priority**: [Explain the value and why it has this priority level]

**Independent Test**: [Describe how this can be tested independently]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]

---

### User Story 3 - [Brief Title] (Priority: P3)

[Describe this user journey in plain language]

**Why this priority**: [Explain the value and why it has this priority level]

**Independent Test**: [Describe how this can be tested independently]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]

---

[Add more user stories as needed, each with an assigned priority]

### Edge Cases

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right edge cases.
-->

- What happens when [boundary condition]?
- How does system handle [error scenario]?

## Requisitos *(obrigatório)*

<!--
  ACTION REQUIRED: Preencha o conteúdo nesta seção com os requisitos funcionais corretos.
  TODOS os requisitos DEVEM incluir:
  - Testes de aceitação (acceptance tests)
  - Mitigações de segurança (OWASP Top 10) se aplicáveis
  - Validação de entrada
-->

### Requisitos Funcionais

- **RF-001**: Sistema DEVE [capacidade específica, e.g., "permitir autenticação de usuários"]
- **RF-002**: Sistema DEVE [capacidade específica, e.g., "validar entrada de dados rigorosamente"]
- **RF-003**: Sistema DEVE [comportamento esperado, e.g., "persistir dados no PostgreSQL"]
- **RF-004**: Sistema DEVE [comportamento, e.g., "registrar eventos de segurança"]
- **RF-005**: [ADICIONE MAIS requisitos conforme necessário]

*Exemplo de requisito com clarificação necessária:*

- **RF-006**: Sistema DEVE autenticar usuários via [CLARIFICAÇÃO NECESSÁRIA: método de autenticação não especificado - JWT, OAuth, sessão?]

### Requisitos de Segurança (OWASP Top 10)

- **RS-001**: Validação rigorosa de entrada de dados em todos os endpoints (OWASP A03 - Injection)
- **RS-002**: Autenticação e autorização implementadas corretamente (OWASP A01, A07)
- **RS-003**: Dados sensíveis criptografados em repouso e em trânsito (OWASP A02)
- **RS-004**: Proteção contra CSRF, XSS e outros ataques comuns [SE APLICÁVEL]
- **RS-005**: Logging e monitoramento de eventos de segurança (OWASP A09)
- **RS-006**: Dependências monitoradas para vulnerabilidades conhecidas (OWASP A06)

### Entidades Principais *(incluir se a feature envolve dados)*

- **[Entidade 1]**: [O que representa, atributos principais]
- **[Entidade 2]**: [O que representa, relacionamentos com outras entidades]

## Critérios de Sucesso *(obrigatório)*

<!--
  ACTION REQUIRED: Defina critérios mensuráveis de sucesso.
  Estes DEVEM ser independentes de tecnologia e mensuráveis.
-->

### Resultados Mensuráveis

- **CS-001**: [Métrica mensurável, e.g., "Usuários completam ação em menos de 2 minutos"]
- **CS-002**: [Métrica mensurável, e.g., "Sistema aguenta 1000 usuários simultâneos"]
- **CS-003**: [Métrica de teste, e.g., "Cobertura de testes acima de 80% para código de negócio"]
- **CS-004**: [Métrica de segurança, e.g., "Todas as vulnerabilidades OWASP Top 10 mitigadas"]
- **CS-005**: Testes PASSAM antes do merge (TDD obrigatório conforme Constituição v1.0.0)

## Premissas

<!--
  ACTION REQUIRED: Preencha com as premissas corretas baseadas nos padrões razoáveis
  escolhidos quando a descrição da feature não especificou certos detalhes.
-->

- [Premissa sobre usuários, e.g., "Usuários têm conectividade estável"]
- [Premissa sobre escopo, e.g., "Suporte mobile fora de escopo para v1"]
- [Premissa sobre dependências, e.g., "Sistema de autenticação existente será reutilizado"]
- [Premissa sobre dados, e.g., "PostgreSQL será utilizado como armazenamento persistente"]
- [Premissa sobre linguagem, e.g., "Todo conteúdo em português brasileiro (pt-BR)"]
