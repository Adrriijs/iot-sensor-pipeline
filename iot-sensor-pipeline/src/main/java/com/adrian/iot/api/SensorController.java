package com.adrian.iot.api;

import com.adrian.iot.model.Alert;
import com.adrian.iot.model.SensorReading;
import com.adrian.iot.repository.AlertRepository;
import com.adrian.iot.repository.SensorReadingRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "IoT Sensor Pipeline", description = "Query sensor readings and alerts")
public class SensorController {

    private final SensorReadingRepository readingRepository;
    private final AlertRepository alertRepository;

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
}