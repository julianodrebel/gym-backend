<!-- SPECKIT START -->
**Contexto de Design**: Leia [Plan: Acompanhamento de Treinos](../../specs/001-acompanhamento-treinos/plan.md) para compreender arquitetura Hexagonal, stack fixo (Java 25, Quarkus, PostgreSQL), modelo de dados e contratos REST.

**Arquitetura**: Domain (entities, repositories ports, usecases, services) → Application (DTOs, ports, services) → Infrastructure (persistence, api, config, security)

**Design Crítico**: Imutabilidade de versões de fichas (nunca editáveis), audit trail (qual versão usada em cada sessão), validação OWASP rigorosa, TDD obrigatório

**Especificação**: [spec.md](../../specs/001-acompanhamento-treinos/spec.md) com 6 histórias de usuário, 20 requisitos funcionais, 8 de segurança OWASP

**Constituição**: v1.0.0 - Clean code, Arquitetura Hexagonal, Testes obrigatórios, Simplicidade (YAGNI), Segurança OWASP, Java 25 + Quarkus + PostgreSQL, Português BR
<!-- SPECKIT END -->
