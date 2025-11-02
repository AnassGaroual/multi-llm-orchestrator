# 🧠 Multi-LLM Orchestrator

[![CI](https://github.com/AnassGaroual/multi-llm-orchestrator/actions/workflows/ci.yml/badge.svg)](https://github.com/AnassGaroual/multi-llm-orchestrator/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**Status:** 🚧 Infrastructure phase - Core orchestration engine in development

Spring Boot foundation for multi-LLM orchestration with production-ready infrastructure.

---

## 🎯 Vision

Orchestrate multiple LLMs (OpenAI, Anthropic, Mistral, Ollama, etc.) through smart workflows, parallel reasoning, and dynamic consensus voting.

---

## ✅ Current Implementation

**Infrastructure (Production-ready):**
- ✅ RFC 9457 Problem Details error handling
- ✅ Correlation ID tracing across requests
- ✅ CORS configuration
- ✅ Security filter chain (permit-all, ready for API keys)
- ✅ Spring Boot 3.5.7 + Java 25
- ✅ Gradle 9.1.0 build system

**DevSecOps Pipeline:**
- ✅ Spotless code formatting (Google Java Format)
- ✅ Git hooks (pre-commit, pre-push, commit-msg)
- ✅ Multi-stage CI/CD (formatting → security → tests)
- ✅ CodeQL + Trivy security scanning
- ✅ JaCoCo coverage reports
- ✅ Docker multi-arch builds
- ✅ Cosign image signing
- ✅ SBOM generation

**In Development:**
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

# Run tests
./gradlew test

# Full quality check
./gradlew fullCheck
```

**Application starts on:** `http://localhost:8080`

**Health check:** `http://localhost:8080/actuator/health`

---

## 📦 Current Architecture (Hexagonal)
```
multi-llm-orchestrator/
├── boot/                        # Application entry point
│   └── MultiLlmOrchestratorApplication.java
│
├── adapters/infra/              # Infrastructure layer
│   ├── errors/                  # RFC 9457 error handling
│   │   ├── ProblemDetailsHandler.java
│   │   └── ProblemTypes.java
│   ├── http/                    # HTTP filters
│   │   └── CorrelationIdFilter.java
│   ├── AppProps.java            # Configuration properties
│   └── SecurityConfig.java      # Security configuration
│
└── [domain/]                    # Coming soon: LLM orchestration core
```

---

## 🧱 Tech Stack

**Core:**
- Java 25 (virtual threads ready)
- Spring Boot 3.5.7
- Spring Security 6.4
- Gradle 9.1.0

**Planned:**
- Spring AI 1.0.3
- PostgreSQL + pgvector
- Redis
- Kafka (event streaming)

**Infrastructure:**
- Docker + Kubernetes ready
- Testcontainers
- Actuator (health checks)

**DevSecOps:**
- Spotless (Google Java Format)
- JaCoCo (70% coverage target)
- OWASP Dependency Check
- CodeQL + Trivy + Gitleaks
- Cosign signing

---

## 🎨 Code Quality

**Automatic enforcement:**
- ✅ Git hooks format on commit
- ✅ CI blocks unformatted code
- ✅ Security scanning on every push

**Commands:**
```bash
# Format code
./gradlew spotlessApply

# Check formatting
./gradlew spotlessCheck

# Install hooks
sh ./install-hooks.sh

# Security scan
./gradlew securityCheck
```

See [docs/FORMATTING.md](docs/FORMATTING.md) for details.

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feat/amazing-feature`
3. Commit changes: `git commit -m "feat: add amazing feature"`
4. Push to branch: `git push origin feat/amazing-feature`
5. Open Pull Request

**Requirements:**
- Follow [Conventional Commits](https://www.conventionalcommits.org/)
- Code must pass Spotless formatting
- Tests must pass
- Coverage target: 70%

---

## 📊 Roadmap

### Phase 1: Foundation ✅ (Current)
- [x] Project structure
- [x] Error handling (RFC 9457)
- [x] Request tracing
- [x] Security basics
- [x] CI/CD pipeline
- [x] Docker image signing

### Phase 2: Core Engine 🚧
- [ ] Spring AI integration
- [ ] Multi-LLM client abstraction
- [ ] Context management
- [ ] Basic orchestration

### Phase 3: Advanced Features
- [ ] Parallel reasoning
- [ ] Consensus voting
- [ ] PostgreSQL + pgvector
- [ ] Redis caching
- [ ] Rate limiting

### Phase 4: Production
- [ ] Kubernetes deployment
- [ ] Kafka event streaming
- [ ] Observability (Prometheus, Grafana)
- [ ] API authentication (JWT/API keys)

---

## 📄 License

MIT License © 2025 Anass Garoual

See [LICENSE](./LICENSE) for details.

---

## 🔗 Links

- [Documentation](docs/)
- [Code Formatting Guide](docs/FORMATTING.md)
- [GitHub Actions](https://github.com/AnassGaroual/multi-llm-orchestrator/actions)
- [Security Policy](SECURITY.md)

---

**Infrastructure-first approach. Core engine coming soon.** 🚀
