package com.trg.fms.service.c.heatbeat;

import com.trg.fms.api.Penalty;
import com.trg.fms.service.c.repo.PenaltyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class KafkaPenaltyConsumer {

    Logger logger = LoggerFactory.getLogger(KafkaPenaltyConsumer.class);
    private final PenaltyRepository penaltyRepository;

    public KafkaPenaltyConsumer(PenaltyRepository penaltyRepository) {
        this.penaltyRepository = penaltyRepository;
    }

    @KafkaListener(topics = "penalty",
            groupId = "group-penalty-consumers",
            containerFactory = "kafkaPenaltyListenerContainerFactory"
    )
    public void listen(@Payload Long penaltyPoints, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Long driverId) {
        logger.info("Received penalty in service c : " + driverId.toString() + " " + penaltyPoints.toString() );

        Penalty newPenalty = new Penalty();
        newPenalty.setDriverId(driverId);
        newPenalty.setTotalPoints(penaltyPoints);

        Mono<Penalty> penaltyToSaveMono = penaltyRepository
                .findByDriverId(driverId)
                .map(existingPenalty -> {
                    existingPenalty.setTotalPoints(penaltyPoints);
                    return existingPenalty;
                }).defaultIfEmpty(newPenalty);

        penaltyToSaveMono.subscribe(penalty -> {
            penaltyRepository.save(penalty).subscribe(savedPenalty -> {
                logger.info("Penalty saved: " + savedPenalty);
            });
        });
    }
}
