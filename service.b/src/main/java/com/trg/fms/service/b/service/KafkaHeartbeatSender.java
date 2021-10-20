package com.trg.fms.service.b.service;

import com.trg.fms.api.Heartbeat;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class KafkaHeartbeatSender {

    private final KafkaTemplate<Long, Heartbeat> kafkaHeartbeatTemplate;
    String kafkaTopic = "heartbeat";

    public KafkaHeartbeatSender(KafkaTemplate<Long, Heartbeat> kafkaHeartbeatTemplate) {
        this.kafkaHeartbeatTemplate = kafkaHeartbeatTemplate;
    }

    public ListenableFuture<SendResult<Long, Heartbeat>> send(Heartbeat heartbeat) {
        return kafkaHeartbeatTemplate.send(kafkaTopic, heartbeat.getId(), heartbeat);
    }
}