package com.adrian.iot.producer;

import com.adrian.iot.model.SensorReading;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SensorProducer {

    private final KafkaTemplate<String, SensorReading> kafkaTemplate;

    @Value("${kafka.topics.raw}")
    private String rawTopic;

    @Value("${kafka.topics.dlq}")
    private String dlqTopic;

    public void publishReading(SensorReading reading) {
        kafkaTemplate.send(rawTopic, reading.getDeviceId(), reading)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish reading from {}: {}", reading.getDeviceId(), ex.getMessage());
                        sendToDlq(reading);
                    } else {
                        log.info("Published: {} -> {}C (partition: {})",
                                reading.getDeviceId(),
                                reading.getTemperature(),
                                result.getRecordMetadata().partition());
                    }
                });
    }

    private void sendToDlq(SensorReading reading) {
        kafkaTemplate.send(dlqTopic, reading.getDeviceId(), reading);
        log.warn("Sent to DLQ: {}", reading.getDeviceId());
    }
}