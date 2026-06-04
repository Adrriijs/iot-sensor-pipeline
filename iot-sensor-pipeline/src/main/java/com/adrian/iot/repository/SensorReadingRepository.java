package com.adrian.iot.repository;

import com.adrian.iot.model.SensorReading;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SensorReadingRepository extends ReactiveMongoRepository<SensorReading, String> {

    Flux<SensorReading> findByDeviceIdOrderByTimestampDesc(String deviceId);

    Mono<SensorReading> findTopByDeviceIdOrderByTimestampDesc(String deviceId);
}