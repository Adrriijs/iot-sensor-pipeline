package com.adrian.iot.api;

import com.adrian.iot.model.Alert;
import com.adrian.iot.model.SensorReading;
import com.adrian.iot.producer.SensorProducer;
import com.adrian.iot.repository.AlertRepository;
import com.adrian.iot.repository.SensorReadingRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "IoT Sensor Pipeline", description = "Query sensor readings and alerts")
public class SensorController {

    private final SensorReadingRepository readingRepository;
    private final AlertRepository alertRepository;
    private final SensorProducer producer;
    private final Random random = new Random();

    private static final List<String> DEVICE_IDS = List.of(
            "device-001", "device-002", "device-003", "device-004", "device-005"
    );

    @GetMapping("/readings")
    @Operation(summary = "Get all sensor readings")
    public Flux<SensorReading> getAllReadings() {
        return readingRepository.findAll();
    }

    @GetMapping("/readings/{deviceId}")
    @Operation(summary = "Get readings by device ID")
    public Flux<SensorReading> getReadingsByDevice(
            @Parameter(description = "Device ID, e.g. device-001")
            @PathVariable String deviceId
    ) {
        return readingRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }

    @GetMapping("/readings/{deviceId}/latest")
    @Operation(summary = "Get the most recent reading for a device")
    public Mono<SensorReading> getLatestReading(@PathVariable String deviceId) {
        return readingRepository.findTopByDeviceIdOrderByTimestampDesc(deviceId);
    }

    @GetMapping("/alerts")
    @Operation(summary = "Get all alerts")
    public Flux<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    @GetMapping("/alerts/{deviceId}")
    @Operation(summary = "Get alerts for a specific device")
    public Flux<Alert> getAlertsByDevice(@PathVariable String deviceId) {
        return alertRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }

    @GetMapping("/alerts/severity/{severity}")
    @Operation(summary = "Get alerts by severity - HIGH or CRITICAL")
    public Flux<Alert> getAlertsBySeverity(@PathVariable String severity) {
        return alertRepository.findBySeverityOrderByTimestampDesc(severity.toUpperCase());
    }

    @PostMapping("/simulate")
    @Operation(summary = "Manually trigger a sensor reading from a random device")
    public Mono<SensorReading> triggerSimulation() {
        String deviceId = DEVICE_IDS.get(random.nextInt(DEVICE_IDS.size()));
        double temperature = Math.round((60 + random.nextDouble() * 32) * 10.0) / 10.0;
        SensorReading reading = SensorReading.builder()
                .deviceId(deviceId)
                .sensorType("TEMPERATURE")
                .temperature(temperature)
                .unit("CELSIUS")
                .timestamp(Instant.now())
                .build();
        producer.publishReading(reading);
        return Mono.just(reading);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get pipeline statistics")
    public Mono<Map<String, Object>> getStats() {
        return Mono.zip(
                readingRepository.count(),
                alertRepository.count(),
                alertRepository.countBySeverity("HIGH"),
                alertRepository.countBySeverity("CRITICAL")
        ).map(t -> Map.of(
                "totalReadings", t.getT1(),
                "totalAlerts", t.getT2(),
                "highAlerts", t.getT3(),
                "criticalAlerts", t.getT4()
        ));
    }
}