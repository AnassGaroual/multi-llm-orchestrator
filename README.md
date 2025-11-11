# 🧠 Multi-LLM Orchestrator

[![CI](https://github.com/AnassGaroual/multi-llm-orchestrator/actions/workflows/ci.yml/badge.svg)](https://github.com/AnassGaroual/multi-llm-orchestrator/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)

**Status:** 🏗️ Domain Model complete - Application layer in development

Distributed AI orchestrator enabling coordination of multiple LLMs via intelligent workflows, parallel reasoning, and dynamic consensus voting. Built with Hexagonal Architecture, DDD, and event-driven design.

---

## 🎯 Vision

Orchestrate multiple LLMs (OpenAI, Anthropic, Mistral, Ollama) through smart workflows with:

- **Parallel Reasoning**: Execute multiple LLM agents concurrently
- **Consensus Voting**: Aggregate responses via dynamic voting strategies
- **Workflow DAGs**: Define complex multi-agent orchestration graphs
- **Multi-Tenancy**: Isolated workflows per tenant with pgvector embeddings
- **Event-Driven**: Kafka-based async communication

---

## ✅ Current Implementation

### **Domain Model (DDD) ✅**

- ✅ Workflow Aggregate Root with immutable state management
- ✅ Node Entity hierarchy (sealed abstract + AgentNode)
- ✅ Value Objects (WorkflowId, NodeId, NodeConstraints, InputMapping, OutputSchema)
- ✅ Domain Events (WorkflowPublished)
- ✅ DAG validation with cycle detection (DFS-based)
- ✅ Framework-independent domain (pure Java)
- ✅ Comprehensive unit tests (15/15 passing, 85%+ coverage)
- ✅ Architecture tests enforcing DDD principles

### **Infrastructure (Production-ready) ✅**

- ✅ RFC 9457 Problem Details error handling
- ✅ Correlation ID tracing across requests
- ✅ CORS configuration
- ✅ Security filter chain (permit-all, ready for API keys)
- ✅ Spring Boot 3.5.7 + Java 25 (virtual threads ready)
- ✅ Gradle 9.1.0 build system

### **DevSecOps Pipeline ✅**

- ✅ Spotless code formatting (Google Java Format)
- ✅ Git hooks (pre-commit, pre-push, commit-msg)
- ✅ Multi-stage CI/CD (formatting → security → tests)
- ✅ CodeQL + Trivy security scanning
- ✅ JaCoCo coverage reports (70%+ target)
- ✅ Docker multi-arch builds
- ✅ Cosign image signing
- ✅ SBOM generation
- ✅ OWASP Dependency Check

### **In Development 🚧**

- 🚧 Application layer (Use Cases + Ports)
- 🚧 Infrastructure adapters (PostgreSQL, Kafka, Spring AI)
- 🚧 REST API (Adapters In)
- 🚧 LLM provider integrations
- 🚧 Spring AI integration
- 🚧 LLM orchestration engine
- 🚧 PostgreSQL + pgvector
- 🚧 Redis caching
- 🚧 Parallel reasoning logic
- 🚧 Consensus voting

---

## 🚀 Quick Start

```bash
# Clone repository
git clone https://github.com/AnassGaroual/multi-llm-orchestrator.git
cd multi-llm-orchestrator

# Run locally
./gradlew bootRun

# Run all tests
./gradlew test

# Full quality check (formatting, tests, coverage)
./gradlew fullCheck

# Security scan
./gradlew securityCheck
```

**Application starts on:** `http://localhost:8080`

**Health check:** `http://localhost:8080/actuator/health`

---

## 📦 Architecture (Hexagonal + DDD)

```
multi-llm-orchestrator/
├── boot/                               # Application entry point
│   └── MultiLlmOrchestratorApplication.java
│
├── domain/                             # ✅ Core business logic (DDD)
│   ├── workflow/                       # Workflow Management bounded context
│   │   ├── Workflow.java               # Aggregate Root
│   │   ├── Node.java                   # Entity (sealed hierarchy)
│   │   ├── AgentNode.java              # Concrete node implementation
│   │   ├── WorkflowStatus.java         # Enum
│   │   └── package-info.java           # Bounded context documentation
│   │
│   ├── shared/                         # Shared kernel
│   │   ├── WorkflowId.java             # Value Object
│   │   ├── NodeId.java                 # Value Object
│   │   ├── NodeConstraints.java        # Value Object
│   │   ├── InputMapping.java           # Value Object
│   │   ├── OutputSchema.java           # Value Object
│   │   ├── ValidationException.java    # Domain exception
│   │   └── CycleDetectedException.java # Domain exception
│   │
│   ├── event/                          # Domain events
│   │   └── WorkflowPublished.java
│   │
│   └── annotation/                     # DDD annotations
│       ├── DomainAggregate.java
│       ├── DomainEntity.java
│       ├── DomainValueObject.java
│       ├── FactoryMethod.java
│       ├── InvariantRule.java
│       └── BoundedContext.java
│
├── application/                        # 🚧 Use Cases + Ports (Coming next)
│   ├── port/in/                        # Driving ports
│   ├── port/out/                       # Driven ports
│   ├── service/                        # Application services
│   └── dto/                            # Data transfer objects
│
├── adapters/                           # Infrastructure & API adapters
│   ├── infra/                          # ✅ Infrastructure
│   │   ├── errors/                     # RFC 9457 error handling
│   │   │   ├── ProblemDetailsHandler.java
│   │   │   └── ProblemTypes.java
│   │   ├── http/                       # HTTP filters
│   │   │   └── CorrelationIdFilter.java
│   │   ├── AppProps.java               # Configuration properties
│   │   └── SecurityConfig.java         # Security configuration
│   │
│   ├── in/rest/                        # 🚧 REST API (Coming soon)
│   ├── out/persistence/                # 🚧 PostgreSQL + pgvector
│   ├── out/messaging/                  # 🚧 Kafka event publishing
│   └── out/ai/                         # 🚧 Spring AI integrations
│
└── architecture/                       # Architecture tests
    ├── DddArchitectureTest.java        # ✅ DDD principles enforcement
    └── HexagonalArchitectureTest.java  # 🚧 Layer dependency rules
```

---

## 🧱 Tech Stack

**Core:**

- **Java 25** - Latest LTS with virtual threads
- **Spring Boot 3.5.7**
- **Spring Security 6.4**
- **Spring AI 1.0.3** - LLM integrations
- **Gradle 9.1.0** - Build automation

**Domain Patterns:**

- **DDD** - Domain-Driven Design (Aggregates, Entities, Value Objects)
- **Hexagonal Architecture** - Ports & Adapters
- **CQRS** - Command Query Responsibility Segregation
- **Event Sourcing** - Domain events

**Persistence:**

- **PostgreSQL** - Primary database
- **pgvector** - Vector embeddings storage
- **Redis** - Caching & session management
- **Liquibase** - Database migrations

**Messaging:**

- **Apache Kafka** - Event streaming
- **Spring Kafka** - Kafka integration

**Testing:**

- **JUnit 5** - Unit testing
- **Mockito** - Mocking framework
- **AssertJ** - Fluent assertions
- **Testcontainers** - Integration tests
- **Java Reflection API** - Architecture tests

**DevSecOps:**

- **Spotless** - Google Java Format enforcement
- **JaCoCo** - Code coverage (70%+ target)
- **OWASP Dependency Check** - Vulnerability scanning
- **CodeQL** - Static analysis
- **Trivy** - Container scanning
- **Gitleaks** - Secret detection
- **Cosign** - Image signing
- **SBOM** - Software Bill of Materials

**Infrastructure:**

- **Docker** - Containerization
- **Kubernetes** - Orchestration
- **GitHub Actions** - CI/CD
- **Actuator** - Health checks & metrics

---

## 🎨 Code Quality Standards

### **Automatic Enforcement**

- ✅ Pre-commit hooks format code before commit
- ✅ Pre-push hooks verify formatting before push
- ✅ Commit-msg hooks enforce Conventional Commits
- ✅ CI pipeline blocks unformatted code
- ✅ Security scanning on every push
- ✅ Coverage gates (70% minimum)

### **Commands**

```bash
# Format all code
./gradlew spotlessApply

# Check formatting
./gradlew spotlessCheck

# Install Git hooks
./gradlew installGitHooks

# Run tests with coverage
./gradlew test jacocoTestReport

# Security vulnerability scan
./gradlew securityCheck

# Full quality check
./gradlew fullCheck
```

### **Architecture Validation**

Custom architecture tests enforce:

- Domain layer has zero framework dependencies
- Domain doesn't depend on application or adapters
- Aggregates don't depend on other aggregates
- Value objects are immutable records
- Factory methods are public static
- Entity fields are private final

See [docs/FORMATTING.md](docs/FORMATTING.md) for complete guidelines.

---

## 🧪 Testing Strategy

### **Test Pyramid**

```
                  ▲
                 / \
                /   \  E2E Tests (Coming soon)
               /─────\
              /       \
             / Integration \ (Testcontainers)
            /───────────────\
           /                 \
          /   Unit Tests      \ (JUnit 5 + Mockito)
         /─────────────────────\
        /                       \
       /   Architecture Tests    \ (Reflection-based)
      /_____________________________\
```

### **Coverage Targets**

- **Domain Layer**: 85%+ (current: 85%)
- **Application Layer**: 80%+ (coming next)
- **Adapters**: 70%+
- **Overall**: 70%+ enforced by CI

### **Test Types**

- **Unit Tests**: Fast, isolated, mocked dependencies
- **Architecture Tests**: DDD/Hexagonal principles enforcement
- **Integration Tests**: Testcontainers for real dependencies
- **E2E Tests**: Full workflow execution (coming soon)

**Current Status:** 125/125 unit tests passing including 10/10 architecture tests

---

## 🤝 Contributing

### **Development Workflow**

1. Fork the repository
2. Create feature branch: `git checkout -b feat/amazing-feature`
3. Make changes following DDD principles
4. Write tests first (TDD)
5. Run quality checks: `./gradlew fullCheck`
6. Commit with Conventional Commits: `git commit -m "feat: add amazing feature"`
7. Push to branch: `git push origin feat/amazing-feature`
8. Open Pull Request

### **Requirements**

- ✅ Follow [Conventional Commits](https://www.conventionalcommits.org/)
- ✅ Code must pass Spotless formatting
- ✅ All tests must pass
- ✅ Coverage target: 70%+ overall
- ✅ Architecture tests must pass
- ✅ No security vulnerabilities
- ✅ Domain layer: zero framework dependencies

### **Commit Message Format**

```
<type>(<scope>): <subject>

feat: A new feature
fix: A bug fix
docs: Documentation only changes
style: Code style changes (formatting)
refactor: Code refactoring
test: Adding tests
chore: Build process or auxiliary tool changes
```

---

## 📊 Roadmap

### Phase 1: Foundation ✅ **COMPLETE**

- [x] Project structure (Hexagonal Architecture)
- [x] Error handling (RFC 9457)
- [x] Request tracing (Correlation IDs)
- [x] Security basics (Spring Security)
- [x] CI/CD pipeline (GitHub Actions)
- [x] Docker image signing (Cosign)
- [x] Code quality gates (Spotless, JaCoCo)
- [x] Git hooks automation

### Phase 2: Domain Model (DDD) ✅ **COMPLETE**

- [x] Workflow Aggregate Root
- [x] Node Entity hierarchy
- [x] Value Objects (IDs, Constraints, Mappings)
- [x] Domain Events
- [x] DAG validation with cycle detection
- [x] Domain layer tests (85%+ coverage)
- [x] Architecture tests (DDD enforcement)
- [x] Bounded context documentation

### Phase 3: Application Layer 🚧 **IN PROGRESS**

- [ ] Use Cases (Commands + Queries)
- [ ] Command/Query Handlers
- [ ] Application Services
- [ ] Input/Output Ports (Interfaces)
- [ ] DTOs & Mappers
- [ ] Application Events
- [ ] CQRS pattern implementation
- [ ] Transaction boundaries

### Phase 4: Infrastructure Adapters

- [ ] REST API (Adapters In)
  - [ ] WorkflowController
  - [ ] Error handling middleware
  - [ ] OpenAPI documentation
- [ ] PostgreSQL Repository (Adapters Out)
  - [ ] JPA entities
  - [ ] Liquibase migrations
  - [ ] Repository implementations
- [ ] Kafka Event Publisher
  - [ ] Event serialization
  - [ ] Topic configuration
  - [ ] Dead letter queues
- [ ] Spring AI Integration
  - [ ] OpenAI adapter
  - [ ] Anthropic adapter
  - [ ] Mistral adapter
  - [ ] Ollama adapter
  - [ ] Provider abstraction

### Phase 5: Advanced Features

- [ ] Parallel Reasoning Engine
- [ ] Consensus Voting Strategies
- [ ] pgvector Integration
- [ ] Redis Caching Layer
- [ ] Rate Limiting
- [ ] Circuit Breakers (Resilience4j)
- [ ] Distributed Tracing (OpenTelemetry)

### Phase 6: Production Hardening

- [ ] Kubernetes Deployment
  - [ ] Helm charts
  - [ ] ConfigMaps & Secrets
  - [ ] Ingress configuration
- [ ] Observability Stack
  - [ ] Prometheus metrics
  - [ ] Grafana dashboards
  - [ ] ELK logging
- [ ] API Authentication
  - [ ] JWT tokens
  - [ ] API keys
  - [ ] OAuth2 integration
- [ ] Multi-Tenancy Isolation
- [ ] Performance Testing (JMH)
- [ ] Chaos Engineering

---

## 🏛️ Design Principles

### **Domain-Driven Design (DDD)**

- **Ubiquitous Language**: Consistent terminology across code and business
- **Bounded Contexts**: Clear boundaries (Workflow Management, Execution, etc.)
- **Aggregates**: Workflow as consistency boundary
- **Entities**: Nodes with identity
- **Value Objects**: Immutable identifiers and configurations
- **Domain Events**: WorkflowPublished, NodeExecuted, etc.
- **Repository Pattern**: Aggregate persistence abstraction

### **Hexagonal Architecture**

- **Domain Layer**: Pure business logic (zero dependencies)
- **Application Layer**: Use cases and orchestration
- **Adapters**: External integrations (REST, DB, Kafka, AI)
- **Ports**: Interfaces defining boundaries (in/out)

### **Clean Code**

- **SOLID Principles**: Single Responsibility, Open/Closed, etc.
- **Immutability**: Records for Value Objects
- **Testability**: Dependency injection, mocking
- **Readability**: Self-documenting code, meaningful names

### **Event-Driven Architecture**

- **Domain Events**: State changes emit events
- **Async Communication**: Kafka for inter-service messaging
- **Event Sourcing**: Audit trail of all state changes (future)

---

## 📈 Metrics & Monitoring

### **Current Metrics**

- **Test Coverage**: 85%+ (domain layer)
- **Build Time**: ~10s (local), ~2min (CI)
- **Security Scan**: 0 critical vulnerabilities
- **Code Quality**: 100% Google Java Format compliance

### **Future Metrics** (Coming with Observability)

- Workflow execution time (p50, p95, p99)
- LLM response latency per provider
- Consensus accuracy rates
- Cache hit ratios
- Event processing lag

---

## 📄 License

MIT License © 2025 Anass Garoual

See [LICENSE](./LICENSE) for details.

---

## 🔗 Links

- **Code Formatting Guide**: [docs/FORMATTING.md](docs/FORMATTING.md)
- **GitHub Actions**: [CI/CD Pipeline](https://github.com/AnassGaroual/multi-llm-orchestrator/actions)
- **Security Policy**: [SECURITY.md](SECURITY.md)

---

**Domain Model complete. Application Layer in progress.** 🚀

_Building a distributed AI orchestrator with DDD, Hexagonal Architecture, and event-driven design._
