package com.adrian.iot.simulator;

import com.adrian.iot.model.SensorReading;
import com.adrian.iot.producer.SensorProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "simulator.enabled", havingValue = "true")
public class SensorSimulator {

    private final SensorProducer producer;
    private final Random random = new Random();

    private static final List<String> DEVICE_IDS = List.of(
            "device-001", "device-002", "device-003", "device-004", "device-005"
    );

    @Scheduled(fixedDelayString = "${simulator.interval-ms}")
    public void simulate() {
        String deviceId = DEVICE_IDS.get(random.nextInt(DEVICE_IDS.size()));
        double temperature = 60 + (random.nextDouble() * 32);

        SensorReading reading = SensorReading.builder()
                .deviceId(deviceId)
                .sensorType("TEMPERATURE")
                .temperature(Math.round(temperature * 10.0) / 10.0)
                .unit("CELSIUS")
                .timestamp(Instant.now())
                .build();

        log.info("Simulating: {} -> {}C", deviceId, reading.getTemperature());
        producer.publishReading(reading);
    }
}