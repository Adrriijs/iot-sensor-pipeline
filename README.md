# IoT Sensor Data Pipeline

A real-time event-driven backend system that ingests, processes, and stores IoT temperature sensor readings using Apache Kafka, Spring Boot WebFlux, and MongoDB.

## Architecture

```
[Sensor Simulator] --> [Kafka: sensor.raw] --> [Ingestor Consumer] --> [MongoDB: sensor_readings]
                                           |
                                           └--> [Alert Consumer] --> [MongoDB: alerts]
                                           |
                                           └--> [sensor.dlq] (failed messages)
```

## Tech Stack

| Layer      | Technology                      |
|------------|---------------------------------|
| Language   | Java 21                         |
| Framework  | Spring Boot 3.5 + WebFlux       |
| Messaging  | Apache Kafka                    |
| Database   | MongoDB (Reactive)              |
| Reactive   | Project Reactor (Mono / Flux)   |
| API Docs   | Springdoc OpenAPI (Swagger UI)  |
| Container  | Docker Compose                  |

## Features

- Simulated IoT sensors publishing temperature readings every 2 seconds
- Kafka topics: sensor.raw, sensor.alerts, sensor.dlq
- Automatic alert generation for HIGH (>75C) and CRITICAL (>85C) readings
- Dead Letter Queue for failed message handling
- Fully reactive MongoDB reads via Spring Data Reactive
- REST API with Swagger UI documentation
- Configurable thresholds and simulator interval via application.yaml

## Getting Started

### Prerequisites
- Java 21
- Maven 3.8+
- Docker + Docker Compose

### 1. Start infrastructure

```bash
docker compose up -d
```

### 2. Run the application

```bash
./mvnw spring-boot:run
```

### 3. Open Swagger UI

```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

| Method | Endpoint                          | Description              |
|--------|-----------------------------------|--------------------------|
| GET    | /api/readings                     | All sensor readings      |
| GET    | /api/readings/{deviceId}          | Readings by device       |
| GET    | /api/readings/{deviceId}/latest   | Most recent reading      |
| GET    | /api/alerts                       | All alerts               |
| GET    | /api/alerts/{deviceId}            | Alerts by device         |
| GET    | /api/alerts/severity/{severity}   | Filter by HIGH or CRITICAL |

## Key Engineering Decisions

**Why Kafka?**
Decouples the simulator from consumers. If a consumer is slow or temporarily down, no data is lost. Kafka buffers messages and consumers catch up when they recover.

**Why two separate consumer groups?**
ingestor-group and alert-group each get a full copy of every message. Adding a new consumer requires zero changes to existing code.

**Why WebFlux + Mono/Flux?**
IoT systems emit high-frequency data. Reactive non-blocking I/O lets one thread handle many concurrent reads and writes without blocking.

**Dead Letter Queue**
Failed messages are routed to sensor.dlq instead of being silently dropped. In production, an ops team would monitor this topic.

## Simulated Devices

Temperature range: 60-92C (randomized every 2 seconds across 5 devices)

- Normal: 60-74C
- High alert: 75-84C
- Critical alert: 85-92C
