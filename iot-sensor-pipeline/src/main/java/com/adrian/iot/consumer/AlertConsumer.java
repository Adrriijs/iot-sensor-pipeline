package com.adrian.iot.consumer;

import com.adrian.iot.model.Alert;
import com.adrian.iot.model.SensorReading;
import com.adrian.iot.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertConsumer {

    private final AlertRepository alertRepository;

    @Value("${alert.temperature.high}")
    private double highThreshold;

    @Value("${alert.temperature.critical}")
    private double criticalThreshold;

    @KafkaListener(
            topics = "${kafka.topics.raw}",
            groupId = "alert-group"
    )
    public void consume(@Payload SensorReading reading) {

        Optional<String> severity = determineSeverity(reading.getTemperature());

        if (severity.isEmpty()) {
            return;
        }

        String severityLevel = severity.get();
        log.warn("ALERT [{}]: {} -> {}C", severityLevel, reading.getDeviceId(), reading.getTemperature());

        Alert alert = Alert.builder()
                .deviceId(reading.getDeviceId())
                .temperature(reading.getTemperature())
                .severity(severityLevel)
                .message(String.format("Device %s reported %sC - %s threshold exceeded",
                        reading.getDeviceId(), reading.getTemperature(), severityLevel))
                .timestamp(Instant.now())
                .build();

        alertRepository.save(alert)
                .subscribe(
                        saved -> log.info("Alert saved: id={} severity={}", saved.getId(), saved.getSeverity()),
                        error -> log.error("Failed to save alert: {}", error.getMessage())
                );
    }

    private Optional<String> determineSeverity(double temperature) {
        if (temperature >= criticalThreshold) {
            return Optional.of("CRITICAL");
        } else if (temperature >= highThreshold) {
            return Optional.of("HIGH");
        }
        return Optional.empty();
    }
}