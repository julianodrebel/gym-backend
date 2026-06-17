# Especificação de Feature: Acompanhamento de Treinos de Musculação

**Branch de Feature**: `001-acompanhamento-treinos`

**Criada**: 2026-06-17

**Status**: Rascunho

**Entrada**: Descrição do usuário: Desenvolver uma aplicação API backend para acompanhamento de treinos de musculação de uso pessoal.

## Cenários de Usuário e Testes *(obrigatório)*

### História de Usuário 1 - Cadastro e Gestão de Exercícios (Priority: P1) 🎯

Como usuário, quero cadastrar exercícios de musculação personalizados para poder criar e gerenciar minha base de dados de exercícios disponíveis nos treinos. Exemplo: Supino reto, Supino inclinado, Agachamento livre, Leg press, Remada curvada.

**Por que esta prioridade**: Exercícios são a base de toda a estrutura de treinos. Sem poder cadastrá-los e gerenciá-los, nenhuma outra funcionalidade é possível. Este é um bloqueador crítico para qualquer outra funcionalidade.

**Teste Independente**: Pode ser totalmente testado pelo fluxo de criação, leitura, edição e inativação de exercícios, entregando valor ao permitir que o usuário mantenha sua base de exercícios.

**Cenários de Aceitação**:

1. **Dado** que o usuário deseja registrar um novo exercício, **Quando** fornece nome, descrição e grupo muscular (e.g., "Supino Reto", "Exercício básico de peito", "Peito"), **Então** o sistema persiste o exercício com status ATIVO e retorna ID único.

2. **Dado** um exercício existente, **Quando** o usuário deseja alterar nome ou descrição, **Então** o sistema atualiza os dados sem afetar histórico de execuções.

3. **Dado** um exercício com histórico de execuções, **Quando** o usuário deseja removê-lo permanentemente, **Então** o sistema oferece opção de INATIVAR (soft delete) para preservar histórico, mantendo referência intacta.

4. **Dado** que o usuário consulta um exercício, **Quando** acessa a página de detalhes, **Então** visualiza histórico de utilização: data primeira execução, data última execução, total de séries realizadas.

---

### História de Usuário 2 - Cadastro e Versionamento de Fichas de Treino (Priority: P1) 🎯

Como usuário, quero criar fichas de treino contendo múltiplos exercícios, com versioning automático para preservar histórico de mudanças. Exemplo: Ficha "Push Pull Legs" em versões (V1, V2, V3) onde cada mudança cria uma nova versão imutável.

**Por que esta prioridade**: Fichas de treino são a segunda camada crítica do sistema. Com exercícios, agora o usuário precisa agrupá-los em planos de treino (fichas). Sem fichas, não há execução de treinos.

**Teste Independente**: Pode ser totalmente testado pelo fluxo de criação de ficha, duplicação, versionamento e ativação de versões, entregando valor ao organizar exercícios em estruturas de treino.

**Cenários de Aceitação**:

1. **Dado** que o usuário deseja criar uma nova ficha de treino, **Quando** fornece nome (e.g., "Push Pull Legs"), **Então** o sistema cria a ficha com V1 (primeira versão), status ATIVA, e lista vazia de exercícios.

2. **Dado** uma ficha existente em versão V1, **Quando** o usuário adiciona, remove ou reordena exercícios e confirma as mudanças, **Então** o sistema cria automaticamente V2 (nova versão) com as alterações, mantendo V1 IMUTÁVEL e completamente intacta.

3. **Dado** uma ficha com múltiplas versões (V1, V2, V3), **Quando** o usuário alterna qual versão é ATIVA, **Então** apenas a versão ativa é utilizada em novos treinos, versões antigas não são alteradas.

4. **Dado** uma versão específica (e.g., V1), **Quando** o usuário executa um treino, **Então** o sistema registra qual versão foi utilizada nessa execução, preservando o histórico correto mesmo que versões posteriores sejam criadas.

5. **Dado** uma versão existente, **Quando** o usuário seleciona "Duplicar", **Então** o sistema cria uma cópia com todos os exercícios, criando uma nova versão (e.g., V4) sem alterar a versão duplicada.

---

### História de Usuário 3 - Execução de Sessão de Treino (Priority: P1) 🎯

Como usuário, quero registrar a execução de um treino: data, horário, exercícios realizados, séries com peso, repetições, e observações gerais. A interface deve ser simples durante a execução para minimizar cliques.

**Por que esta prioridade**: Execução de treinos é o núcleo da aplicação. Sem poder registrar execuções, não há dados para histórico ou sugestões de progressão. Este é o valor principal da aplicação.

**Teste Independente**: Pode ser totalmente testado pelo fluxo de iniciar treino, executar exercício, registrar séries, salvar observações e encerrar sessão, entregando o valor central de registro de atividade.

**Cenários de Aceitação**:

1. **Dado** uma ficha de treino ativa, **Quando** o usuário inicia um novo treino (clicando "Iniciar Treino"), **Então** o sistema cria nova sessão com data/hora atual, associa à versão ativa da ficha, e exibe o primeiro exercício.

2. **Dado** uma sessão em execução, **Quando** o usuário registra uma série (peso, repetições, observações específicas do exercício), **Então** o sistema persiste e avança para a próxima série ou próximo exercício.

3. **Dado** uma série registrada, **Quando** o usuário confirma, **Então** o sistema inicia automaticamente um cronômetro de descanso (padrão 60s, configurável por exercício).

4. **Dado** uma sessão em execução, **Quando** o usuário completa todos os exercícios da ficha, **Então** oferece opção de adicionar observações gerais da sessão (e.g., "Pouca energia", "Dor no ombro") e salvar.

5. **Dado** uma sessão salva, **Quando** o usuário consulta, **Então** visualiza data, duração, exercícios realizados, séries de cada exercício, e observações - tudo em formato não-editável (audit trail).

---

### História de Usuário 4 - Histórico e Comparação de Desempenho (Priority: P2)

Como usuário, quero visualizar para cada exercício: última execução, melhor carga registrada, evolução de cargas ao longo do tempo, evolução do volume (peso × reps), e histórico completo de todas as execuções.

**Por que esta prioridade**: Com execuções registradas, o histórico fornece inteligência para decisões futuras e motivação ao visualizar progresso. Crítico mas não bloqueia a execução inicial de treinos.

**Teste Independente**: Pode ser totalmente testado consultando dados de múltiplas execuções de um exercício e verificando cálculos de evolução, entregando visualização de progresso.

**Cenários de Aceitação**:

1. **Dado** um exercício com múltiplas execuções, **Quando** o usuário consulta detalhes, **Então** visualiza última execução (data, peso, reps, série número).

2. **Dado** um exercício, **Quando** o usuário visualiza histórico, **Então** vê lista de todas as séries registradas ordenadas por data, com peso, repetições, data e hora.

3. **Dado** múltiplas execuções do mesmo exercício ao longo do tempo, **Quando** o usuário consulta evolução, **Então** visualiza gráfico de evolução de carga máxima por período (semana/mês) e volume total.

4. **Dado** histórico de um exercício, **Quando** o usuário compara dois treinos (e.g., há 1 mês vs. hoje), **Então** visualiza claramente a progressão de carga e volume.

---

### História de Usuário 5 - Sugestão Automática de Progressão de Carga (Priority: P2)

Como usuário, quero receber sugestões automáticas de progressão de carga baseadas no meu desempenho anterior, respeitando uma faixa alvo de repetições (8-12 reps) para cada exercício.

**Por que esta prioridade**: Sugestões automáticas agregam inteligência ao sistema, auxiliando o usuário a evoluir de forma programada. Importante mas não bloqueia execução inicial.

**Teste Independente**: Pode ser testado aplicando regras de progressão a um histórico de séries e verificando sugestões retornadas, entregando orientação de evolução.

**Cenários de Aceitação**:

1. **Dado** múltiplas séries do mesmo exercício, **Quando** todas as séries da última sessão atingem ≥12 reps (limite superior da faixa), **Então** o sistema sugere aumento de carga (e.g., +2,5kg ou +5%).

2. **Dado** séries com desempenho abaixo de 8 reps (limite inferior), **Quando** o usuário consulta sugestão, **Então** o sistema recomenda manutenção da carga atual (não aumentar, focar em atingir 8-12).

3. **Dado** séries dentro da faixa alvo (8-12 reps), **Quando** o usuário consulta sugestão, **Então** o sistema recomenda manutenção da carga e progressão de repetições.

4. **Dado** um exercício sem histórico, **Quando** o usuário consulta sugestão, **Então** o sistema responde "Sem histórico - comece com carga confortável".

---

### História de Usuário 6 - Cronômetro de Descanso (Priority: P3)

Como usuário, quero um cronômetro automático de descanso após completar cada série para garantir intervalo apropriado entre séries e manter o treino produtivo.

**Por que esta prioridade**: Cronômetro melhora experiência durante o treino mas não é crítico. Pode ser implementado após histórico estar funcional.

**Teste Independente**: Pode ser testado verificando que cronômetro inicia após série, permite pausar/reiniciar, e respeita tempo configurado por exercício.

**Cenários de Aceitação**:

1. **Dado** uma série registrada, **Quando** o usuário confirma, **Então** cronômetro inicia automaticamente com tempo padrão (60 segundos).

2. **Dado** cronômetro em execução, **Quando** usuário clica "Pausar", **Então** cronômetro pausa mantendo tempo restante visível.

3. **Dado** exercício específico, **Quando** o usuário configura tempo de descanso padrão (e.g., 90s para supino, 45s para rosca), **Então** próximas séries deste exercício usam novo tempo.

---

### Casos Extremos

- O que acontece quando o usuário tenta adicionar uma série com peso ou repetições negativas? Sistema DEVE rejeitar e exibir mensagem de validação clara.

- O que acontece quando uma versão de ficha é duplicada após múltiplas alterações? Sistema DEVE copiar exatamente o estado atual da versão, incluindo todos os exercícios e suas posições.

- O que acontece se o usuário tenta modificar uma versão antiga de ficha? Sistema DEVE rejeitar (versões são imutáveis), sugerindo criação de nova versão se mudanças são desejadas.

- Como o sistema se comporta se usuário inicia treino mas não completa? Sessão DEVE ser salva no estado "EM_PROGRESSO" e poder ser retomada ou marcada como cancelada.

---

## Requisitos *(obrigatório)*

### Requisitos Funcionais (MVP)

- **RF-001**: Sistema DEVE permitir criação de exercício com nome, descrição e grupo muscular.

- **RF-002**: Sistema DEVE permitir edição de nome e descrição de exercício sem afetar histórico.

- **RF-003**: Sistema DEVE permitir inativação (soft delete) de exercício preservando histórico de execuções.

- **RF-004**: Sistema DEVE exibir para cada exercício: data primeira execução, data última execução, total de séries realizadas.

- **RF-005**: Sistema DEVE permitir criação de ficha de treino com nome e lista inicial vazia de exercícios.

- **RF-006**: Sistema DEVE criar automaticamente nova versão (V1 → V2, V2 → V3, etc.) ao modificar exercícios de uma ficha.

- **RF-007**: Sistema DEVE manter todas as versões antigas IMUTÁVEIS - nenhuma versão anterior pode ser alterada ou deletada.

- **RF-008**: Sistema DEVE permitir ativação de qualquer versão anterior como versão "ativa" para próximos treinos.

- **RF-009**: Sistema DEVE registrar qual versão de ficha foi utilizada em cada execução de treino para audit trail correto.

- **RF-010**: Sistema DEVE permitir duplicação de versão existente criando nova versão com cópia de todos os exercícios.

- **RF-011**: Sistema DEVE permitir iniciar novo treino a partir de ficha ativa, criando sessão com data/hora atual.

- **RF-012**: Sistema DEVE permitir registrar série com peso, repetições e observações específicas durante execução de treino.

- **RF-013**: Sistema DEVE iniciar cronômetro de descanso automaticamente após confirmação de série.

- **RF-014**: Sistema DEVE permitir registrar observações gerais da sessão ao finalizar.

- **RF-015**: Sistema DEVE exibir para cada exercício: última execução, melhor carga, evolução de cargas, evolução de volume, histórico completo.

- **RF-016**: Sistema DEVE calcular e sugerir progressão automática: aumento de carga se todas séries ≥12 reps; manutenção se <8 reps; manutenção se 8-12 reps.

- **RF-017**: Sistema DEVE permitir pausar e reiniciar cronômetro de descanso.

- **RF-018**: Sistema DEVE permitir configurar tempo de descanso padrão por exercício.

- **RF-019**: Sistema DEVE exibir dashboard básico com fichas ativas, última sessão, e próximas sessões sugeridas.

- **RF-020**: Sistema DEVE validar rigorosamente entrada de peso e repetições (não-negativos, tipos numéricos corretos).

### Requisitos de Segurança (OWASP Top 10)

- **RS-001**: Validação rigorosa de entrada de dados em todos os endpoints (OWASP A03 - Injection) - nomes de exercícios, observações, números de séries/peso/reps devem ser validados e sanitizados.

- **RS-002**: Autenticação obrigatória mesmo para single user (futuro-proof para múltiplos usuários) - requer JWT ou similar (OWASP A01, A07).

- **RS-003**: Autorização verificada em cada operação - usuário só acessa seus próprios dados (OWASP A01 - Broken Access Control).

- **RS-004**: Dados sensíveis (senhas, tokens) criptografados em repouso e em trânsito via HTTPS (OWASP A02 - Cryptographic Failures).

- **RS-005**: Logging estruturado de todas as operações críticas: criação de exercício, inicio de treino, modificação de ficha (OWASP A09 - Logging/Monitoring).

- **RS-006**: Dependências Java/Quarkus/PostgreSQL monitoradas regularmente para vulnerabilidades (OWASP A06 - Vulnerable Components).

- **RS-007**: Proteção contra CSRF em endpoints de escrita (POST, PUT, DELETE).

- **RS-008**: Limites de rate limiting para evitar abuso de API.

### Entidades Principais *(Domain Model)*

- **Exercício**: Representa um exercício de musculação. Atributos: ID, nome, descrição, grupo muscular (string), status (ATIVO/INATIVO), data criação, data última modificação.

- **Ficha de Treino**: Representa uma estrutura de treinamento. Atributos: ID, nome, versão ativa, status (ATIVA/INATIVA), data criação.

- **Versão da Ficha**: Cada alteração cria nova versão. Atributos: ID, número versão (V1, V2...), lista de exercícios, data criação, status (IMUTÁVEL sempre).

- **Sessão de Treino**: Execução de uma ficha em um momento específico. Atributos: ID, data, hora início, hora término, versão ficha utilizada, observações gerais, lista de exercícios executados, status (EM_PROGRESSO/FINALIZADO/CANCELADO).

- **Exercício Executado**: Execução de um exercício durante sessão. Atributos: ID, exercício ID, ordem execução, observações específicas, lista de séries, duração.

- **Série Executada**: Cada série realizada. Atributos: ID, número série, peso, repetições, horário execução, exercício executado ID.

---

## Critérios de Sucesso *(obrigatório)*

### Resultados Mensuráveis

- **CS-001**: Usuário consegue cadastrar exercício em menos de 30 segundos através de formulário simples.

- **CS-002**: Usuário consegue criar ficha de treino e adicionar 10 exercícios em menos de 2 minutos.

- **CS-003**: Usuário consegue iniciar treino, registrar 3 séries de 5 exercícios, e encerrar em menos de 15 minutos (interface otimizada para agilidade).

- **CS-004**: Sistema exibe histórico de exercício em menos de 500ms (sem lag noticível).

- **CS-005**: Sugestão de progressão é calculada e retornada em menos de 100ms.

- **CS-006**: Cobertura de testes acima de 80% para código de negócio (domain layer).

- **CS-007**: Todas as 7 mitigações OWASP Top 10 aplicáveis estão implementadas e testadas.

- **CS-008**: API retorna 99.5% de requisições bem-sucedidas em carga normal (sem erros não-tratados).

- **CS-009**: Dados históricos de exercícios nunca são perdidos (imutabilidade respeitada).

- **CS-010**: Testes passam 100% antes de merge em branch principal (zero testes falhando).

---

## Premissas

- Inicialmente aplicação suporta apenas UM usuário, mas arquitetura permite expansão futura para múltiplos usuários através de namespace/tenant ID.

- Autenticação será baseada em JWT com refresh token para sagualar extensibilidade.

- PostgreSQL será o banco relacional (conforme Constituição v1.0.0).

- Todos os dados (exercícios, fichas, histórico) devem ser persistidos - nenhum dado é descartável ou temporário.

- Validações complexas (OWASP, regras de negócio) serão implementadas na camada Domain (Arquitetura Hexagonal).

- Operações durante execução de treino (registrar série, inicia cronômetro) terão endpoints separados para permitir UI ágil com chamadas independentes.

- Histórico de versões de fichas é crítico - nunca alterar versão antiga, sempre criar versão nova.

- Sugestão de progressão usa regra simples inicial (8-12 reps) e pode ser expandida futuramente.

- Todo conteúdo textual em português brasileiro (pt-BR) conforme Constituição v1.0.0.

- Primeira versão foca em API backend - interface mobile/web será desenvolvida posteriormente.
