package com.adrian.iot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sensor_readings")
public class SensorReading {

    @Id
    private String id;

    private String deviceId; 
    private String sensorType;  
    private Double temperature;
    private String unit; 
    private Instant timestamp;
}