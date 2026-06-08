package com.adrian.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IotSensorPipelineApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotSensorPipelineApplication.class, args);
	}

}
