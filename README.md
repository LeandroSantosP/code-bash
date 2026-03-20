# CodeBash

> Your code's worst nightmare — an AI-powered code reviewer that doesn't sugarcoat.

CodeBash is a brutally honest, AI-powered code review API that delivers instant, entertaining, and genuinely constructive feedback on your code. Submit your code, enable **Roast Mode** for maximum sarcasm, and get categorized feedback with fix suggestions. Compete on the Shame Leaderboard to see how your code stacks up against the worst on the internet.

## Features

- **Code Submission** — Paste code snippets and receive instant quality ratings
- **Roast Mode** — Toggle maximum sarcasm for entertaining code analysis
- **Detailed Analysis** — Categorized feedback by severity (critical, warning, good)
- **Fix Suggestions** — Visual diffs demonstrating improvements
- **Shame Leaderboard** — Compete for the title of worst code on the internet

## Tech Stack

- **Java 21** with **Spring Boot 4.0**
- **PostgreSQL 16** for persistence
- **Ollama** for AI-powered code analysis
- **Lombok** for boilerplate reduction
- **Maven** for build management

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker (for PostgreSQL)
- [Ollama](https://ollama.ai/) running locally

### Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd bug-bash
   ```

2. **Start PostgreSQL with Docker**
   ```bash
   docker compose up -d
   ```

3. **Start Ollama**
   ```bash
   ollama serve
   # In another terminal:
   ollama pull codellama
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8080`.

## API Endpoints

### Submit Code for Review

```bash
curl -X POST http://localhost:8080/bash-code \
  -H "Content-Type: application/json" \
  -d '{"code": "function test(){console.log(\"hello\")}", "roastMode": true}'
```

**Response:**
```json
{
  "feedbackMessage": "Congratulations! You've written code that would make a medieval scribe weep...",
  "severity": "high",
  "score": 2
}
```

### Health Check

```bash
curl http://localhost:8080/bash-code/health
```

## Configuration

Configuration is managed via `src/main/resources/application.yaml`. Key settings:

| Property | Description | Default |
|----------|-------------|---------|
| `spring.datasource.url` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/bug_bash` |
| `spring.datasource.username` | Database username | `postgres` |
| `spring.datasource.password` | Database password | `postgres` |

## Project Structure

```
src/main/java/com/leandrosps/bug_bash/
├── Application.java              # Spring Boot entry point
├── app/
│   ├── BashCodeController.java   # REST API endpoints
│   ├── HttpClient.java           # Ollama API client
│   ├── SubmissionsRepository.java # Database repository
│   └── entites/
│       ├── Analysis.java        # Analysis entity
│       └── Submission.java      # Submission entity
└── entriesobj/
    ├── CodeReviewResponse.java  # AI response DTOs
    └── OllamaResponse.java      # Ollama API response
```

## Development

### Running Tests
```bash
mvn test
```

### Building
```bash
mvn clean package
```

## License

MIT
