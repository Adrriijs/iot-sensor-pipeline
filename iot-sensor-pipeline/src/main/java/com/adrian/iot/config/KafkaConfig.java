package com.adrian.iot.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.raw}")
    private String rawTopic;

    @Value("${kafka.topics.alerts}")
    private String alertsTopic;

    @Value("${kafka.topics.dlq}")
    private String dlqTopic;

    @Bean
    public NewTopic sensorRawTopic() {
        return TopicBuilder.name(rawTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic sensorAlertsTopic() {
        return TopicBuilder.name(alertsTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic sensorDlqTopic() {
        return TopicBuilder.name(dlqTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}