
# 🧠 Multi-LLM Orchestrator

[![CI](https://github.com/AnassGaroual/multi-llm-orchestrator/actions/workflows/ci.yml/badge.svg)](https://github.com/AnassGaroual/multi-llm-orchestrator/actions/workflows/ci.yml)
[![Code Quality](https://github.com/AnassGaroual/multi-llm-orchestrator/actions/workflows/code-quality.yml/badge.svg)](https://github.com/AnassGaroual/multi-llm-orchestrator/actions/workflows/code-quality.yml)
[![codecov](https://codecov.io/gh/AnassGaroual/multi-llm-orchestrator/branch/main/graph/badge.svg)](https://codecov.io/gh/AnassGaroual/multi-llm-orchestrator)

An open-source **AI collaboration engine** built with **Spring Boot 3.5.7 + Spring AI + Java 25**.

Its goal is to **orchestrate multiple LLMs** (OpenAI, Anthropic, Mistral, Ollama, etc.)  
through smart workflows, parallel reasoning, and dynamic consensus voting.

---

## 🚀 Getting Started

```bash

# Run locally
./gradlew bootRun

# Or with Docker
docker compose up --build
````

---

## 🧱 Stack

* **Java 25**, **Spring Boot 3.5.7**, **Spring AI**
* **PostgreSQL + pgvector**, **Redis**
* **Gradle 9.1**, **Docker**, **JUnit 5**, **OpenTelemetry**

---

## ⚖️ License

Licensed under the **MIT License © 2025 Anass Garoual**.
See the [LICENSE](./LICENSE) file for details.

---

## 🎨 Code Formatting

This project uses [Spotless](https://github.com/diffplug/spotless) with **Google Java Format**.

---

**Automatic enforcement:**
- ✅ Git hooks format on commit
- ✅ CI blocks unformatted code
- ✅ IDE plugin keeps editor in sync

See [docs/FORMATTING.md](docs/FORMATTING.md) for complete guide.

---

### Quick Commands
```bash
# Apply formatting
./gradlew spotlessApply

# Check formatting
./gradlew spotlessCheck

# Install git hooks
./install-hooks.sh
```
