package com.trg.fms.service.c.streams;

import com.trg.fms.api.Heartbeat;
import com.trg.fms.api.HeartbeatType;
import com.trg.fms.service.c.serde.GenericAvroSerde;
import com.trg.fms.service.c.serde.PenaltyAggregateSerde;
import com.trg.fms.service.c.streams.aggregates.PenaltyAggregate;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
import org.springframework.kafka.core.CleanupConfig;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Value("${service.a.bootstrap.servers.config}")
    private String bootstrapServers;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "penaltyStreams");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public StreamsBuilderFactoryBeanConfigurer configurer() {
        return fb -> {
            fb.setCleanupConfig(new CleanupConfig(true, true));
            fb.setStateListener((newState, oldState) -> {
                System.out.println("State transition from " + oldState + " to " + newState);
            });
        };
    }

    @Bean
    public KStream<Long, Long> kStream(StreamsBuilder streamsBuilder) {


        final KStream<Long, Heartbeat> heartbeats = streamsBuilder.stream("heartbeat",
                Consumed.with(Serdes.Long(), new GenericAvroSerde<>(Heartbeat.class)));

        PenaltyAggregateSerde penaltyAggregateSerde = new PenaltyAggregateSerde();

        final KStream<Long, Long> penaltyCountsStream =
                heartbeats
                        .groupByKey()
                        .aggregate(PenaltyAggregate::new,
                                (key, value, aggregate) -> {
                                    return new PenaltyAggregate().copy(aggregate).withHeartbeat(value);
                                },
                                Materialized.with(Serdes.Long(), penaltyAggregateSerde))
                        .mapValues((readOnlyKey, value) -> value.calculatePoints())
                        .toStream();


        penaltyCountsStream.to("penalty", Produced.with(Serdes.Long(), Serdes.Long()));

        return penaltyCountsStream;

    }

    private Long calculatePenaltyPoints(Heartbeat heartbeat) {
        return 0L;
    }

}