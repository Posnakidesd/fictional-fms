package com.trg.fms.service.b.config;

import com.trg.fms.api.Trip;
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
public class KafkaTripConsumerConfig {

    @Value("${service.a.bootstrap.servers.config}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<Long, Trip> tripConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-trip-consumers");
        return new DefaultKafkaConsumerFactory<>(props,
                new LongDeserializer(),
                new AvroDeserializer<>(Trip.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, Trip> kafkaTripListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, Trip>
                factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(tripConsumerFactory());
        return factory;
    }
}