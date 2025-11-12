# Domain Model - DDD Architecture

## Overview

Multi-LLM Orchestrator domain model following Domain-Driven Design tactical patterns.

## Bounded Contexts

### 1. Workflow Management

- **Aggregate Root**: `Workflow`
- **Entities**: `Node` (sealed: AgentNode, FanoutNode, ReduceNode, VetoNode, VoteNode, LoopNode)
- **Value Objects**: `ExecutionGraph`, `NodeConstraints`, `InputMapping`, `OutputSchema`, `QualityScore`

**Responsibilities:**

- Workflow definition and validation
- DAG construction and cycle detection
- Node topology management

### 2. Execution Engine

- **Aggregate Root**: `ExecutionPlan` (TODO)
- **Value Objects**: `ExecutionContext`, `ExecutionMetrics`

**Responsibilities:**

- Workflow execution orchestration
- State management
- Progress tracking

### 3. Consensus Engine

- **Aggregate Root**: `ConsensusSession` (TODO)
- **Entities**: `Vote`
- **Value Objects**: `ConsensusResult`, `QuorumRequirement`

**Responsibilities:**

- Multi-agent consensus formation
- Quality assessment
- Voting strategies

### 4. Optimization

- **Value Objects**: `Budget`, `TokenBudget`, `CostBudget`

**Responsibilities:**

- Resource allocation
- Cost optimization

## Design Principles

### SOLID

- **SRP**: Each class has one responsibility
- **OCP**: Extensible via sealed interfaces (Node types)
- **LSP**: All nodes are substitutable
- **ISP**: Small, focused interfaces
- **DIP**: Domain depends on abstractions

### DDD Tactical Patterns

- **Aggregates**: Consistency boundaries (Workflow)
- **Entities**: Identity-based (Node)
- **Value Objects**: Immutable, no identity (QualityScore, Budget)
- **Domain Services**: Stateless operations (CycleDetectionService)
- **Domain Events**: State changes (WorkflowPublished)

### Thread Safety

- **Immutability**: All domain objects immutable (records, defensive copies)
- **Builder Pattern**: Safe updates without mutation
- **Virtual Threads Ready**: No shared mutable state

### Security

- **Input Validation**: All constructors validate
- **Provider Whitelisting**: Only allowed LLM providers
- **Tenant Isolation**: Multi-tenant support built-in
- **No Information Leakage**: Error codes instead of details

## Invariants

### Workflow

1. Must be a DAG (no cycles)
2. Entry node must exist
3. All node references must be valid
4. Immutable after publication

### Node

1. All next node references must exist in workflow
2. Provider format must be valid (provider:model)

### Budget

1. Used tokens/cost cannot exceed max
2. All values must be non-negative

## Events

### WorkflowPublished

- Emitted when workflow is published
- Contains: workflowId, correlationId, aggregateVersion
- Kafka-ready with full metadata

## Future Extensions

### Phase 2

- ExecutionPlan aggregate
- ConsensusSession aggregate
- Vote entity

### Phase 3

- ResourceAllocator aggregate
- CacheManager aggregate

### Phase 4 

- ResonanceSession aggregate
- WorkflowEvolutionEngine aggregate
- HypothesisTestingLoop aggregate
