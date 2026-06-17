<!-- Sync Impact Report: Constitution v1.0.0 created on 2026-06-17 
     Initial release with 5 core principles focused on clean code, hexagonal architecture, and mandatory testing.
     Sections: Core Principles, Stack Tecnológico Fixo, Requisitos de Segurança, Fluxo de Desenvolvimento, Governance
     All content in Brazilian Portuguese (pt-BR)
-->

# Constituição do Gym Backend

## Princípios Fundamentais

### I. Código Limpo e Qualidade de Código
O código DEVE ser legível, mantível e seguir as convenções de clean code. Nomes DEVEM ser autoexplicativos e o código DEVE ser organizado para fácil compreensão. Repetição DEVE ser evitada através de abstrações apropriadas. Métodos DEVEM ter responsabilidade única e clara. A qualidade de código é não-negociável: revisões de código DEVEM validar clareza, simplicidade e coesão.

### II. Arquitetura Hexagonal (Ports & Adapters)
A aplicação DEVE estar estruturada segundo o padrão de Arquitetura Hexagonal. Camadas DEVEM estar claramente separadas: Domain (núcleo da lógica de negócio), Application (casos de uso), Ports (interfaces que definem contratos) e Adapters (implementações de I/O). Dependências DEVEM apontar para dentro (do exterior para o núcleo). Testes DEVEM verificar que limites de camada estão respeitados. Nenhuma dependência de frameworks ou bibliotecas externas DEVE vazar para a camada de domínio.

### III. Testes Obrigatórios (NÃO-NEGOCIÁVEL)
Testes são obrigatórios e não-negociáveis. Nenhum caso de uso é entregue sem seus testes. Test-Driven Development (TDD) é fortemente recomendado: testes são escritos primeiro, então a implementação. Testes DEVEM cobrir cenários de sucesso, falha e edge cases. Testes de unidade, integração e aceitação DEVEM estar presentes conforme apropriado. Cobertura de testes abaixo de 80% para código de negócio DEVE ser justificada explicitamente.

### IV. Simplicidade e Justificação de Complexidade
A resposta padrão para "devemos adicionar esta abstração?" é NÃO. O princípio YAGNI (You Aren't Gonna Need It) DEVE guiar decisões arquiteturais. Complexidade DEVE ser justificada: se uma solução é mais complexa, a justificativa pela complexidade DEVE ser evidente e documentada. Prefira soluções simples e diretas. Refatore apenas quando houver motivo claro e comprovado.

### V. Segurança e Conformidade com OWASP
A aplicação DEVE aplicar as mitigações do OWASP Top 10 aplicáveis a um serviço backend. Validação de entrada DEVE ser rigorosa em todas as camadas. Autenticação e autorização DEVEM estar implementadas corretamente. Dados sensíveis (senhas, tokens) DEVEM ser protegidos. Logging e monitoramento DEVEM registrar eventos de segurança. Vulnerabilidades conhecidas em dependências DEVEM ser monitoradas regularmente.

## Stack Tecnológico Fixo
As seguintes escolhas tecnológicas são fixas para este projeto e NÃO DEVEM ser sobrescritas por feature:

- **Linguagem**: Java 25 (preview features apenas com justificativa explícita)
- **Framework**: Quarkus (versão estável mais recente compatível com Java 25)
- **Banco de Dados Relacional**: PostgreSQL
- **Build**: Maven ou Gradle (conforme configuração inicial do Quarkus)

Alterações ao stack tecnológico exigem revisão explícita e documentação no Governance.

## Requisitos de Segurança
A aplicação DEVE implementar as seguintes mitigações do OWASP Top 10:

- **A01:2021 – Broken Access Control**: Validação rigorosa de autorização em cada endpoint
- **A02:2021 – Cryptographic Failures**: Dados sensíveis criptografados; comunicação via HTTPS
- **A03:2021 – Injection**: Prepared statements; validação de entrada rigorosa
- **A04:2021 – Insecure Design**: Ameaças modeladas durante design; padrões de segurança documentados
- **A05:2021 – Security Misconfiguration**: Configuração segura padrão; revisão periódica
- **A06:2021 – Vulnerable Components**: Dependências monitoradas; patches aplicados
- **A07:2021 – Authentication Failures**: Autenticação forte; tokens seguros; rotação de credenciais
- **A08:2021 – Software/Data Integrity**: Integridade de código verificada; dependências confiáveis
- **A09:2021 – Logging/Monitoring**: Auditoria de eventos de segurança; alertas acionáveis
- **A10:2021 – SSRF**: Validação de URLs; restrição de recursos

## Fluxo de Desenvolvimento
Testes DEVEM ser implementados conforme cada feature é desenvolvida, seguindo o ciclo:
1. Caso de uso é descrito e testabilidade é validada
2. Testes são escritos (Red)
3. Implementação é codificada (Green)
4. Código é refatorado conforme necessário (Refactor)
5. Revisão de código valida conformidade com princípios e segurança

Toda mudança ao código DEVE ter peer review antes de merge na branch principal.

## Linguagem e Documentação
Todo conteúdo textual produzido no contexto deste projeto, incluindo código, comentários, documentação, commit messages e issues, DEVE ser redigido em português brasileiro (pt-BR). Exceções: comentários em código podem usar nomes técnicos em inglês quando não houver equivalente claro em português.

## Governance
Esta Constituição é o documento de autoridade para decisões arquiteturais e de qualidade. Nenhuma feature ou mudança significativa pode violar estes princípios.

**Conformidade**: Todas as Pull Requests DEVEM verificar conformidade com esta Constituição. Reviewers DEVEM rejeitar código que viole estes princípios.

**Versioning**: Mudanças significativas a esta Constituição usam Semantic Versioning (MAJOR.MINOR.PATCH).

**Emendas**: Alterações a esta Constituição exigem documentação clara e consenso entre leads técnicos. Cada emenda é registrada com data e motivo.

**Versão**: 1.0.0 | **Ratificada**: 2026-06-17 | **Última Emenda**: 2026-06-17
