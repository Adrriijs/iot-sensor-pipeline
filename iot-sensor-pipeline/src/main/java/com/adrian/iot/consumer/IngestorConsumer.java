package com.adrian.iot.consumer;

import com.adrian.iot.model.SensorReading;
import com.adrian.iot.repository.SensorReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IngestorConsumer {

    private final SensorReadingRepository repository;

    @KafkaListener(
            topics = "${kafka.topics.raw}",
            groupId = "ingestor-group"
    )
    public void consume(
            @Payload SensorReading reading,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("Ingestor received: {} | {}C | partition={} offset={}",
                reading.getDeviceId(), reading.getTemperature(), partition, offset);

        repository.save(reading)
                .subscribe(
                        saved -> log.info("Saved reading: id={}", saved.getId()),
                        error -> log.error("Failed to save reading: {}", error.getMessage())
                );
    }
}