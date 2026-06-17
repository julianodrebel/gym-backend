# Pesquisa e Clarificação: Acompanhamento de Treinos

**Propósito**: Resolver unknowns técnicos identificados no plano

**Criada**: 2026-06-17

**Status**: Completo

---

## Decisões de Design

### 1. Versionamento Imutável em Quarkus/JPA

**Unknown**: Como implementar imutabilidade de versões de fichas?

**Decisão**: Soft Delete + Audit Fields
- Cada versão tem `deleted_at` = NULL (ativa) ou timestamp (inativa)
- Adicionar constraints no banco: `UNIQUE (ficha_id, versao, deleted_at IS NULL)`
- Validação em domain layer: rejeitar updates/deletes em versões
- JPA entities: usar `@Version` para optimistic locking

**Implementação**:
```java
@Entity
public class VersaoFichaTreino {
    @Id private UUID id;
    @Column(nullable = false) private UUID fichaTreinoId;
    @Column(nullable = false) private Integer numero; // V1, V2, V3
    @Column(nullable = false) private LocalDateTime criadoEm;
    @Column private LocalDateTime deletadoEm;
    @Version private Long versao; // Optimistic locking
    
    // Validação em domain:
    public void validarNaoModificavel() {
        if (deletadoEm != null) {
            throw new VersaoImutavelException("Versão foi deletada");
        }
    }
}
```

**Rationale**: Preserva histórico, permite queries eficientes, não quebra referências estrangeiras

---

### 2. Sugestão de Progressão - Algoritmo

**Unknown**: Como calcular progressão automática?

**Decisão**: Regra 8-12 RMs (Repetição Máxima)
- Target: 8-12 repetições por série
- Se TODAS as séries da última sessão ≥ 12 reps → sugerir +2.5kg (ou +5%)
- Se alguma série < 8 reps → sugerir manter carga (focar em chegar a 8)
- Se 8 ≤ reps ≤ 12 → sugerir manter carga, progressão de reps

**Implementação**:
```java
public class ProgressaoService {
    public SugestaoProgresso calcularProgressao(Exercicio exercicio) {
        List<SerieExecutada> ultimasSeries = repositorio.
            obterUltimas5Series(exercicio);
        
        double minReps = ultimasSeries.stream()
            .mapToDouble(SerieExecutada::getReps)
            .min().orElse(0);
            
        if (minReps >= 12) {
            return SugestaoProgresso.aumentarCarga(5); // +5%
        } else if (minReps < 8) {
            return SugestaoProgresso.manterCarga();
        } else {
            return SugestaoProgresso.manterCargaProgressarReps();
        }
    }
}
```

**Rationale**: Simples, baseado em literatura científica de treino de força, fácil de usar

---

### 3. Cronômetro de Descanso

**Unknown**: Como implementar cronômetro em REST API?

**Decisão**: Client-side Timer + Server-side Validation
- Cliente rastreia tempo decorrido localmente
- Quando usuário registra próxima série, servidor valida intervalo mínimo
- Se intervalo < 30s desde última série, avisar "Descanso insuficiente"
- Será implementado como metadata na resposta

**Implementação**:
```java
@PostMapping("/sessoes/{id}/series")
public ResponseEntity<RegistrarSerieResponse> registrarSerie(
    @PathVariable UUID id,
    @RequestBody RegistrarSerieRequest request) {
    
    SessaoTreino sessao = servicoExecutarTreino.obterSessao(id);
    ExercicioExecutado ultimoExercicio = sessao.obterUltimoExercicio();
    
    if (ultimoExercicio != null) {
        long segundosDesdeUltima = Duration.between(
            ultimoExercicio.obterHorarioUltimaSerieConfirmada(),
            Instant.now()
        ).getSeconds();
        
        if (segundosDesdeUltima < 30) {
            return ResponseEntity.ok()
                .body(new RegistrarSerieResponse(
                    serie,
                    "Aviso: Descanso insuficiente (> 30s recomendado)"
                ));
        }
    }
    
    servicoExecutarTreino.registrarSerie(id, request);
    return ResponseEntity.created(...).body(...);
}
```

**Rationale**: Simples, não requer WebSocket, cliente controla UX

---

### 4. Arquitetura Hexagonal em Quarkus

**Unknown**: Como estruturar Hexagonal Architecture em Quarkus?

**Decisão**: 3 Camadas Claras
- **Domain**: Entities, Repositories (interfaces), UseCases, Services, Exceptions (sem imports de Quarkus/JPA)
- **Application**: DTOs, Application Services (orquestração), Ports (interfaces dos adapters secundários)
- **Infrastructure**: Implementações de repositórios, Controllers REST, Security, Config

**Package Structure**:
```
com.gymbackend.
  domain/
    entities/
    repositories/       ← Interfaces (ports)
    usecases/
    services/
    exceptions/
  application/
    dto/
    ports/              ← Interfaces de adapters
    services/           ← Application services
  infrastructure/
    persistence/        ← Implementações de repo
    api/                ← Controllers
    config/
    security/
```

**Rationale**: Separação clara de responsabilidades, facilita testes, respeita princípios SOLID

---

### 5. Autenticação JWT + Single User

**Unknown**: Como estruturar JWT para single user?

**Decisão**: JWT com Refresh Token (futuro-proof multi-user)
- Token de acesso: válido por 15 minutos
- Token de refresh: válido por 7 dias
- Claims: `sub` (user ID), `exp`, `iat`
- Mesmo em single user, usar userId fixo ou UUID específico

**Implementação**:
```java
@Configuration
public class SecurityConfig {
    public String gerarToken(String userId) {
        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 900000)) // 15 min
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }
}
```

**Premissa**: Mesmo que MVP seja single user, base está pronta para multi-user

---

### 6. Performance: Consulta de Histórico

**Unknown**: Como otimizar consultas de histórico de séries?

**Decisão**: Indexação + Paginação
- Índices em PostgreSQL: `(exercicio_id, data_criacao DESC)` para histórico
- Índice em `sessao_id` para rápido acesso a séries da sessão
- Paginação: 50 séries por página (padrão)
- Query otimizada com JPA Projection

**SQL**:
```sql
CREATE INDEX idx_series_exercicio_data 
ON serie_executada(exercicio_id, horario_execucao DESC);

CREATE INDEX idx_series_sessao 
ON serie_executada(sessao_treino_id);
```

**JPA**:
```java
@Query("""
    SELECT new SerieDTO(s.id, s.peso, s.repeticoes, s.horarioExecucao)
    FROM SerieExecutada s
    WHERE s.exercicio.id = :exercicioId
    ORDER BY s.horarioExecucao DESC
    LIMIT 50 OFFSET :offset
""")
List<SerieDTO> obterHistoricoSeriesComPaginacao(
    @Param("exercicioId") UUID exercicioId,
    @Param("offset") int offset);
```

**Rationale**: <500ms p95 para consultas de histórico

---

### 7. Migração de Dados (Flyway)

**Unknown**: Como versionarDados?

**Decisão**: Flyway migrations com versionamento semântico
- V001__initial_schema.sql → estrutura base
- V002__indexes_and_constraints.sql → otimizações
- V003__add_audit_columns.sql → para auditoriaFutura

**Rationale**: Reproduzibilidade, rollback seguro, versionamento de schema

---

## Decisões de Segurança OWASP

### A01 - Broken Access Control
- **Implementação**: Validação de userId em cada endpoint
- **Teste**: Tentar acessar dados de outro usuário (mesmo em single-user, preparado para multi)

### A02 - Cryptographic Failures
- **Implementação**: Senhas bcrypted; tokens JWT HS512
- **HTTPS**: Obrigatório em produção

### A03 - Injection
- **Implementação**: JPA com Prepared Statements (paranoid)
- **Validação**: Regex/whitelisting para nomes de exercícios

### A06 - Vulnerable Components
- **Implementação**: Dependabot em CI/CD
- **Revisão**: Trimestral de dependências Maven

### A07 - Authentication Failures
- **Implementação**: JWT com refresh token; rate limiting em login

### A09 - Logging & Monitoring
- **Implementação**: SLF4J + Logback estruturado
- **Log**: Criar exercício, iniciar sessão, modificar ficha, etc.

---

## Conclusões

**Decisões Confirmadas**:
- ✅ Versionamento Imutável via Soft Delete + constraints
- ✅ Progressão via 8-12 RMs rule
- ✅ Cronômetro client-side
- ✅ Arquitetura Hexagonal 3 camadas em Quarkus
- ✅ JWT com refresh token
- ✅ Paginação com índices PostgreSQL
- ✅ Flyway para migrations
- ✅ OWASP integrado

**Não-Aplicáveis (MVP)**:
- ❌ WebSocket (usar polling/client timer)
- ❌ Cache distribuído (usar cache local Quarkus)
- ❌ Event sourcing (usar audit tables simples)
- ❌ CQRS (single database model)

**Próxima Fase**: data-model.md com entidades e relacionamentos detalhados
