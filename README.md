# USPS Vehicle Scanner API

A Java + Spring Boot REST API that uses OpenAI Vision (GPT-4o) to analyze USPS LLV mail truck photos for damage, tire issues, fluid leaks, and other safety hazards. Returns a structured `PASS` / `ATTENTION` / `UNSAFE` verdict with details.

Built as the Java backend companion to my [React Native vehicle inspection app](https://github.com/samcraw1/usps-vehicle-inspection).

## Features

- **`POST /api/inspect`** — upload a vehicle photo, get an AI-powered inspection report
- **`GET /api/history`** — last 20 inspections (in-memory)
- **`DELETE /api/history`** — clear history
- **`GET /api/health`** — health check
- **Built-in HTML test UI** at `/` — drag-and-drop a photo, see the analysis live
- Configurable model (`gpt-4o-mini` by default, swap to `gpt-4o` for max accuracy)
- Mock fallback if API key is missing or call fails — never crashes
- CORS enabled for cross-origin frontend integration

## Tech Stack

- **Java 17** + **Spring Boot 3.3** (web + test)
- **Jackson** for JSON serialization
- **JUnit 5** + **MockMvc** + **Mockito** for tests
- **OpenAI Vision (GPT-4o / GPT-4o-mini)** for image analysis
- Maven build

## Architecture

```
HTTP Request
    ↓
InspectionController         (handles routing, multipart parsing)
    ↓
OpenAIService                (base64 encode → call OpenAI → parse response)
    ↓
InspectionHistoryService     (in-memory record of recent inspections)
```

Three packages: `controller/` (HTTP), `service/` (business logic), `model/` (data shapes).

## Run It Locally

### Requirements
- Java 17+
- Maven 3.9+
- An OpenAI API key

### Setup

```bash
git clone https://github.com/samcraw1/usps-vehicle-scanner-api
cd usps-vehicle-scanner-api
export OPENAI_API_KEY=sk-proj-yourkeyhere
mvn spring-boot:run
```

Then visit http://localhost:8080 in your browser.

### Test it with curl

```bash
curl -X POST -F "image=@/path/to/vehicle.jpg" http://localhost:8080/api/inspect
```

### Run the tests

```bash
mvn test
```

## Example Response

```json
{
  "status": "UNSAFE",
  "issues": [
    {
      "issue": "Significant body damage",
      "location": "Front right corner",
      "severity": "HIGH"
    },
    {
      "issue": "Headlight missing",
      "location": "Front right",
      "severity": "HIGH"
    }
  ],
  "summary": "Vehicle is unsafe due to significant front damage and missing headlight."
}
```

## Configuration

`src/main/resources/application.properties`:

| Property | Default | Description |
|----------|---------|-------------|
| `openai.api.key` | (env: `OPENAI_API_KEY`) | Your OpenAI API key |
| `openai.model` | `gpt-4o-mini` | Vision model to use |
| `spring.servlet.multipart.max-file-size` | `10MB` | Max upload size |

## Built By

Sam Crawford — currently a USPS CCA studying for the Computer Analyst/Programmer Associate role. This project demonstrates Java, Spring Boot, REST APIs, AI/ML integration, and full-stack capability.
