# Modelo de Dados: Acompanhamento de Treinos

**Propósito**: Definir entidades de domínio, relacionamentos, validações e estados

**Criada**: 2026-06-17

**Baseado em**: [research.md](research.md)

---

## Diagrama de Relacionamentos (ER)

```
┌─────────────────┐
│   Exercicio     │ (base de dados de exercícios)
├─────────────────┤
│ id (PK)         │
│ nome            │
│ descricao       │
│ grupo_muscular  │
│ status          │
│ criado_em       │
│ atualizado_em   │
│ deletado_em     │
└────────┬────────┘
         │ 1:N
         │
    ┌────┴──────────────────────────────┐
    │                                   │
    ▼ 1:N (versões)                  ▼ 1:N (séries)
┌──────────────────────┐        ┌─────────────────┐
│  VersaoFichaTreino   │        │ SerieExecutada  │
├──────────────────────┤        ├─────────────────┤
│ id (PK)              │        │ id (PK)         │
│ ficha_id (FK)        │        │ exercicio_exe   │
│ numero (V1,V2...)    │        │ _id (FK)        │
│ exercicios[] (M:N)   │        │ numero_serie    │
│ criado_em            │        │ peso            │
│ deletado_em          │        │ repeticoes      │
│ status (IMUTÁVEL)    │        │ horario_exec    │
└──────────────────────┘        └─────────────────┘
         ▲ 1:1 (ativa)
         │
    ┌────┴──────────────┐
    │                   │
    │ 1:N               │
    │ (usadas em)       │
    │                   │
┌───┴──────────────┐    │
│  FichaTreino     │    │
├──────────────────┤    │
│ id (PK)          │    │
│ usuario_id (FK)  │    │ 1:N
│ nome             │◄───┴────┐
│ versao_ativa_id  │ 1:N     │
│ criado_em        │ (sessões)
│ atualizado_em    │         │
└──────────────────┘         │
                             │
                     ┌───────┴──────────────┐
                     │                      │
                 ┌───┴─────────────────┐   │
                 │  SessaoTreino       │   │
                 ├──────────────────────┤  │
                 │ id (PK)              │  │
                 │ versao_ficha_id (FK) │  │
                 │ data                 │  │
                 │ hora_inicio          │  │
                 │ hora_fim             │  │
                 │ observacoes_gerais   │  │
                 │ status               │  │
                 └──┬────────────────────┘  │
                    │ 1:N (exercícios     │
                    │ nesta sessão)       │
                    │                      │
            ┌───────┴──────────────────┐  │
            │ ExercicioExecutado       │  │
            ├──────────────────────────┤  │
            │ id (PK)                  │  │
            │ sessao_id (FK)           │  │
            │ exercicio_id (FK)        │  │
            │ ordem_execucao           │  │
            │ observacoes              │  │
            │ duration_min             │  │
            │ series[] (1:N)           │  │
            └──────────────────────────┘  │
                                           │
                                    [1:N]
```

---

## Entidades Detalhadas

### 1. Exercício

**Responsabilidade**: Representar um exercício de musculação reutilizável

**Atributos**:

```java
@Entity
@Table(name = "exercicios")
public class Exercicio {
    @Id
    private UUID id;
    
    @Column(nullable = false, length = 255)
    private String nome; // "Supino Reto"
    
    @Column(nullable = false, length = 1000)
    private String descricao; // "Exercício básico para peito"
    
    @Column(nullable = false, length = 100)
    private String grupoMuscular; // "Peito", "Costas", "Pernas", etc.
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusExercicio status; // ATIVO, INATIVO
    
    @Column(nullable = false)
    private LocalDateTime criadoEm;
    
    @Column(nullable = false)
    private LocalDateTime atualizadoEm;
    
    @Column
    private LocalDateTime deletadoEm; // Soft delete
    
    @Version
    private Long versao; // Optimistic locking
}

enum StatusExercicio {
    ATIVO, INATIVO
}
```

**Validações de Domínio**:
- Nome: não vazio, max 255 caracteres
- Descrição: não vazia, max 1000 caracteres
- GrupoMuscular: enum ou lista predefinida
- Ao inativar: verificar se há histórico de execuções (manter intacto)

**Estados**:
- ATIVO: Pode ser usado em fichas
- INATIVO: Não pode ser usado em fichas novas, histórico preservado

---

### 2. Ficha de Treino

**Responsabilidade**: Agrupar exercícios em estrutura reutilizável

**Atributos**:

```java
@Entity
@Table(name = "fichas_treino")
public class FichaTreino {
    @Id
    private UUID id;
    
    @Column(nullable = false)
    private UUID usuarioId; // Para futuro multi-user
    
    @Column(nullable = false, length = 255)
    private String nome; // "Push Pull Legs"
    
    @ManyToOne
    @JoinColumn(name = "versao_ativa_id")
    private VersaoFichaTreino versaoAtiva;
    
    @Column(nullable = false)
    private LocalDateTime criadoEm;
    
    @Column(nullable = false)
    private LocalDateTime atualizadoEm;
    
    @OneToMany(mappedBy = "fichaTreino")
    private List<VersaoFichaTreino> versoes = new ArrayList<>();
    
    @Version
    private Long versao;
}
```

**Validações**:
- Nome: não vazio, max 255 caracteres
- Versão ativa: sempre aponta para uma versão válida

---

### 3. Versão da Ficha de Treino

**Responsabilidade**: Manter histórico imutável de mudanças de fichas

**Atributos**:

```java
@Entity
@Table(name = "versoes_ficha_treino", 
       uniqueConstraints = @UniqueConstraint(
           name = "uk_ficha_versao_ativa",
           columnNames = {"ficha_id", "numero"},
           where = "deletado_em IS NULL"
       ))
public class VersaoFichaTreino {
    @Id
    private UUID id;
    
    @Column(nullable = false)
    private UUID fichaId;
    
    @Column(nullable = false)
    private Integer numero; // 1, 2, 3, ...
    
    @ManyToMany
    @JoinTable(
        name = "versoes_exercicios",
        joinColumns = @JoinColumn(name = "versao_id"),
        inverseJoinColumns = @JoinColumn(name = "exercicio_id")
    )
    @OrderColumn(name = "ordem_execucao")
    private List<Exercicio> exercicios = new ArrayList<>();
    
    @Column(nullable = false)
    private LocalDateTime criadoEm;
    
    @Column
    private LocalDateTime deletadoEm; // Soft delete (never update)
    
    @Column(nullable = false)
    private String status; // "IMUTÁVEL" (semantics, não enumerada)
    
    @Version
    private Long versao;
}
```

**Validações**:
- Uma vez criada, NUNCA pode ser atualizada (versao é final)
- Soft delete só marca `deletado_em` (nunca apaga)
- Constraint UNIQUE: `(ficha_id, numero, deletado_em IS NULL)`

**Imutabilidade**:
```java
public class VersaoFichaTreino {
    public void validarNaoModificavel() {
        if (deletadoEm != null) {
            throw new VersaoImutavelException(
                "Versão foi deletada e não pode ser modificada"
            );
        }
    }
    
    public void adicionarExercicio(Exercicio e) {
        validarNaoModificavel();
        throw new VersaoImutavelException(
            "Versões são imutáveis após criação"
        );
    }
}
```

---

### 4. Sessão de Treino

**Responsabilidade**: Registrar execução de uma versão de ficha em momento específico

**Atributos**:

```java
@Entity
@Table(name = "sessoes_treino")
public class SessaoTreino {
    @Id
    private UUID id;
    
    @Column(nullable = false)
    private UUID usuarioId; // Para futuro multi-user
    
    @Column(nullable = false)
    private UUID versaoFichaTreinoId; // Qual versão foi usada
    
    @Column(nullable = false)
    private LocalDate data;
    
    @Column(nullable = false)
    private LocalTime horaInicio;
    
    @Column
    private LocalTime horaFim;
    
    @Column(length = 2000)
    private String observacoesGerais;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSessao status; // EM_PROGRESSO, FINALIZADO, CANCELADO
    
    @OneToMany(mappedBy = "sessaoTreino", cascade = CascadeType.ALL)
    private List<ExercicioExecutado> exercicios = new ArrayList<>();
    
    @Column(nullable = false)
    private LocalDateTime criadoEm;
    
    @Column(nullable = false)
    private LocalDateTime atualizadoEm;
    
    @Version
    private Long versao;
}

enum StatusSessao {
    EM_PROGRESSO,   // Treino em andamento
    FINALIZADO,     // Concluído com sucesso
    CANCELADO       // Abortado / não finalizado
}
```

**Validações**:
- VersaoFichaTreinoId: deve existir e ser válida
- HoraFim: deve ser após horaInicio (se presente)
- Status: transições válidas: EM_PROGRESSO → FINALIZADO | CANCELADO

---

### 5. Exercício Executado

**Responsabilidade**: Registro de execução de um exercício dentro de sessão

**Atributos**:

```java
@Entity
@Table(name = "exercicios_executados")
public class ExercicioExecutado {
    @Id
    private UUID id;
    
    @Column(nullable = false)
    private UUID sessaoTreinoId;
    
    @Column(nullable = false)
    private UUID exercicioId; // Referência ao exercício cadastrado
    
    @Column(nullable = false)
    private Integer ordemExecucao; // 1, 2, 3, ...
    
    @Column(length = 1000)
    private String observacoes; // "Senti dor no ombro", etc.
    
    @Column
    private Integer durracaoMinutos; // Duração desta sessão do exercício
    
    @OneToMany(mappedBy = "exercicioExecutado", cascade = CascadeType.ALL)
    @OrderBy("numeroSerie ASC")
    private List<SerieExecutada> series = new ArrayList<>();
    
    @Column(nullable = false)
    private LocalDateTime criadoEm;
    
    @Version
    private Long versao;
}
```

**Validações**:
- ExercicioId: deve existir e estar ATIVO
- OrdemExecucao: >= 1, único dentro da sessão
- Series: mínimo 1 série por exercício

---

### 6. Série Executada

**Responsabilidade**: Registro de uma série individual realizada

**Atributos**:

```java
@Entity
@Table(name = "series_executadas")
public class SerieExecutada {
    @Id
    private UUID id;
    
    @Column(nullable = false)
    private UUID exercicioExecutadoId;
    
    @Column(nullable = false)
    private Integer numeroSerie; // 1, 2, 3, ...
    
    @Column(nullable = false)
    private BigDecimal peso; // kg (DECIMAL para precisão)
    
    @Column(nullable = false)
    private Integer repeticoes; // número de reps
    
    @Column(nullable = false)
    private LocalDateTime horarioExecucao; // Quando foi realizada
    
    @Column(nullable = false)
    private LocalDateTime criadoEm;
    
    @Version
    private Long versao;
}
```

**Validações**:
- Peso: >= 0, max 500kg (validação de domain)
- Repetições: >= 0, max 100 (validação de domain)
- NumeroSerie: >= 1, único dentro do exercício
- HorarioExecucao: <= agora

**Regras de Negócio**:
- Não pode ser editada após criação (séries são imutáveis)
- Peso e repetições usados para calcular progressão

---

## Relacionamentos

| Origem | Destino | Tipo | Descrição |
|--------|---------|------|-----------|
| FichaTreino | VersaoFichaTreino | 1:N | Uma ficha tem múltiplas versões |
| VersaoFichaTreino | Exercicio | M:N | Versão contém múltiplos exercícios |
| SessaoTreino | VersaoFichaTreino | N:1 | Sessão usa uma versão específica (audit trail) |
| SessaoTreino | ExercicioExecutado | 1:N | Sessão contém múltiplos exercícios realizados |
| ExercicioExecutado | SerieExecutada | 1:N | Exercício contém múltiplas séries |

---

## Restrições e Índices

```sql
-- Imutabilidade de versões
ALTER TABLE versoes_ficha_treino 
ADD CONSTRAINT ck_versao_imutavel 
CHECK (deletado_em IS NULL OR deletado_em <= criado_em);

-- Audit trail: qual versão foi usada
ALTER TABLE sessoes_treino
ADD CONSTRAINT fk_sessao_versao
FOREIGN KEY (versao_ficha_treino_id) 
REFERENCES versoes_ficha_treino(id);

-- Performance: histórico de séries
CREATE INDEX idx_series_exercicio_data 
ON series_executadas(exercicio_id, horario_execucao DESC);

-- Performance: séries de uma sessão
CREATE INDEX idx_series_exercicio_exec 
ON series_executadas(exercicio_executado_id, numero_serie);

-- Performance: sessões de uma ficha
CREATE INDEX idx_sessoes_ficha_data 
ON sessoes_treino(versao_ficha_treino_id, data DESC);
```

---

## Estados de Transição

### Exercício
```
       ┌─────────────────────┐
       │ CRIAR EXERCÍCIO     │
       └──────────┬──────────┘
                  │
            ┌─────▼─────┐
            │   ATIVO   │◄──────────┐
            └─────┬─────┘           │
                  │            REATIVAR
              INATIVAR         │
                  │            │
            ┌─────▼─────┐      │
            │ INATIVO   ├──────┘
            └───────────┘
```

### Ficha de Treino
```
Criação V1 → Edição (cria V2) → Edição (cria V3) → ... (versionamento infinito)
Sempre existe uma versão ATIVA
```

### Sessão de Treino
```
┌──────────────────┐
│  EM_PROGRESSO    │ (padrão ao criar)
└────┬──────────────┘
     │
     ├─────────────────────────────┐
     │                             │
FINALIZAR                     CANCELAR
     │                             │
     ▼                             ▼
┌──────────────┐             ┌──────────┐
│ FINALIZADO   │             │ CANCELADO│
└──────────────┘             └──────────┘
```

---

## Invariantes

1. **Imutabilidade de Versões**: Uma versão de ficha nunca pode ser modificada após criação
2. **Audit Trail**: Toda sessão registra qual versão foi usada (para histórico correto)
3. **Histórico de Exercícios**: Nunca são deletados, apenas inativados
4. **Séries Não-Editáveis**: Uma série, uma vez registrada, não pode ser modificada
5. **Status de Sessão**: Uma sessão não pode voltar de FINALIZADO para EM_PROGRESSO
6. **Usuário Imutável**: Uma sessão sempre pertence ao mesmo usuário (futura validação multi-user)

---

## Próxima Fase

Interface Contracts em `contracts/` com DTOs REST correspondentes
