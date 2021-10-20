package com.trg.fms.service.b.heatbeat;

import com.trg.fms.api.Heartbeat;
import com.trg.fms.api.HeartbeatType;
import com.trg.fms.api.Trip;
import com.trg.fms.service.b.service.KafkaHeartbeatSender;
import com.trg.fms.service.b.service.KafkaHeartbeatTriggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTripConsumer {

    Logger logger = LoggerFactory.getLogger(KafkaTripConsumer.class);

    @Autowired
    KafkaHeartbeatTriggerService kafkaHeartbeatTriggerService;

    @KafkaListener(topics = "trip", groupId = "group-trip-consumers", containerFactory = "kafkaTripListenerContainerFactory")
    public void listen(Trip trip) {
        logger.info("Received trip : " + trip);
        kafkaHeartbeatTriggerService.scheduleHeartbeat(trip);
    }
}
