package com.trg.fms.service.a.service;

import com.trg.fms.api.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import reactor.core.publisher.Mono;

@Service
public class KafkaTripProducerService {
    @Autowired
    private KafkaTemplate<Long, Trip> kafkaTripTemplate;

    String kafkaTopic = "trip";

    public Mono<SendResult<Long, Trip>> send(Trip trip) {

        ListenableFuture<SendResult<Long, Trip>> result = kafkaTripTemplate.send(kafkaTopic, trip.getId(), trip);

        return Mono.fromFuture(result.completable());
    }
}