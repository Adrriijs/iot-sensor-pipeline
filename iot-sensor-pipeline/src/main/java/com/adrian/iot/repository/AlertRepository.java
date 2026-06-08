package com.adrian.iot.repository;

import com.adrian.iot.model.Alert;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AlertRepository extends ReactiveMongoRepository<Alert, String> {

    Flux<Alert> findByDeviceIdOrderByTimestampDesc(String deviceId);

    Flux<Alert> findBySeverityOrderByTimestampDesc(String severity);

    Mono<Long> countBySeverity(String severity);
}