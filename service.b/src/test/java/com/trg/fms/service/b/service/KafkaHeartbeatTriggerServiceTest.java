package com.trg.fms.service.b.service;

import com.trg.fms.api.Heartbeat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KafkaHeartbeatTriggerServiceTest {

    @Autowired
    KafkaHeartbeatTriggerService kafkaHeartbeatTriggerService;

    /**
     * Not really useful test
     */
    @Test
    public void testGenerateRandomHeartbeat() {

        Heartbeat testHeartbeat = kafkaHeartbeatTriggerService.generateRandomHeartbeat(1L);
        assertEquals(1L, testHeartbeat.getId());
        assertNotNull(testHeartbeat.getDistance());
        assertNotNull(testHeartbeat.getType());
    }
}