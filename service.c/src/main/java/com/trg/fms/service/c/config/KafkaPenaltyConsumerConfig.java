package com.trg.fms.service.c.config;

import com.trg.fms.api.Heartbeat;
import com.trg.fms.serdes.AvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaPenaltyConsumerConfig {

    @Value("${service.a.bootstrap.servers.config}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<Long, Long> penaltyConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-heartbeat-consumers");
        return new DefaultKafkaConsumerFactory<>(props,
                new LongDeserializer(),
                new LongDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, Long> kafkaPenaltyListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, Long>
                factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(penaltyConsumerFactory());
        return factory;
    }
}