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

### 1. Install Java 21 (laptop terminal)

> Run this in your system terminal (not VS Code) so the JDK is installed globally.

```bash
brew install --cask temurin@21
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

To make it permanent, add the `export` line to `~/.zshrc`.

### 2. Start infrastructure

```bash
cd iot-sensor-pipeline
docker compose up -d
```

Wait ~10 seconds for Kafka to be ready before the next step.

### 3. Run the application

```bash
./mvnw spring-boot:run
```

You should see simulator logs appearing every 2 seconds:
```
Simulating: device-003 -> 78.4C
ALERT [HIGH]: device-003 -> 78.4C
Published: device-003 -> 78.4C (partition: 0)
```

### 4. Open the live dashboard

```
http://localhost:8080
```

The dashboard auto-refreshes every 2 seconds and shows device readings, alerts, and pipeline stats.
You can also hit **Trigger Reading** to manually publish a sensor event.

### Stopping

```bash
# Ctrl+C the app, then:
docker compose down
```

## API Endpoints

| Method | Endpoint                          | Description                        |
|--------|-----------------------------------|------------------------------------|
| GET    | /api/readings                     | All sensor readings                |
| GET    | /api/readings/{deviceId}          | Readings by device                 |
| GET    | /api/readings/{deviceId}/latest   | Most recent reading                |
| GET    | /api/alerts                       | All alerts                         |
| GET    | /api/alerts/{deviceId}            | Alerts by device                   |
| GET    | /api/alerts/severity/{severity}   | Filter by HIGH or CRITICAL         |
| POST   | /api/simulate                     | Manually trigger a sensor reading  |
| GET    | /api/stats                        | Pipeline stats (counts)            |

## Key Engineering Decisions

**Kafka:** Decouples the simulator from consumers. If a consumer is slow or temporarily down, no data is lost. Kafka buffers messages and consumers catch up when they recover.

**Separate consumer groups:** `ingestor-group` and `alert-group` each get a full copy of every message. Adding a new consumer requires zero changes to existing code.

**WebFlux + Mono/Flux:** IoT systems emit high-frequency data. Reactive non-blocking I/O lets one thread handle many concurrent reads and writes without blocking.

**Dead Letter Queue:** Failed messages are routed to `sensor.dlq` instead of being silently dropped. In production, an ops team would monitor this topic.

## Simulated Devices

Temperature range: 60-92C (randomized every 2 seconds across 5 devices)

- Normal: 60-74C
- High alert: 75-84C
- Critical alert: 85-92C
