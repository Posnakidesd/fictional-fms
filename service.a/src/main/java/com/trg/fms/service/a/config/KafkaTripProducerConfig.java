package com.trg.fms.service.a.config;

import com.trg.fms.api.Trip;
import com.trg.fms.serdes.AvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebFlux
public class KafkaTripProducerConfig {

    @Value("${service.a.bootstrap.servers.config}")
    private String bootstrapServers;


    @Bean
    public ProducerFactory<Long, Trip> tripProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    @Bean
    public KafkaTemplate<Long, Trip> kafkaTripTemplate() {
        return new KafkaTemplate<>(tripProducerFactory());
    }
}