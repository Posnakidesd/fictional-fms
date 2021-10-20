package com.trg.fms.service.c.heatbeat;

import com.trg.fms.api.Heartbeat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class KafkaHeartbeatConsumer {

    Logger logger = LoggerFactory.getLogger(KafkaHeartbeatConsumer.class);

    @KafkaListener(topics = "heartbeat",
            groupId = "group-heartbeat-consumers",
            containerFactory = "kafkaHeartListenerContainerFactory"
    )
    public void listen(@Payload Heartbeat heartbeat, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Long driverId) {

        logger.info("Received heartbeat in service c : " + heartbeat);
        logger.info("Driver id : " + driverId);
    }
}