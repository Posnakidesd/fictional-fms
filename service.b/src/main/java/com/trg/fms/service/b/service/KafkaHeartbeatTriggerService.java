package com.trg.fms.service.b.service;

import com.trg.fms.api.Heartbeat;
import com.trg.fms.api.HeartbeatType;
import com.trg.fms.api.Trip;
import com.trg.fms.api.TripState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class KafkaHeartbeatTriggerService {

    Logger logger = LoggerFactory.getLogger(KafkaHeartbeatTriggerService.class);

    private final KafkaHeartbeatSender kafkaHeartbeatSender;
    private final Map<Long, ScheduledFuture<?>> trips = new HashMap<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    public KafkaHeartbeatTriggerService(KafkaHeartbeatSender kafkaHeartbeatSender) {
        this.kafkaHeartbeatSender = kafkaHeartbeatSender;
    }

    public void scheduleHeartbeat(Trip trip) {
        Runnable heartbeatTask = () -> {
            kafkaHeartbeatSender.send(generateRandomHeartbeat(trip.getDriverId()));
        };
        if(trips.containsKey(trip.getId())) {
            if(trip.getState().equals(TripState.STOP)) {
                logger.info("Stopping trip with id = " + trip.getId());
                trips.get(trip.getId()).cancel(true);
                trips.remove(trip.getId());
            } else {
                logger.info("Trip already in progress. Nothing to do");
            }
        } else {
            logger.info("Scheduling heartbeats for trip id " + trip.getId());
            ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(heartbeatTask, 0, 3, TimeUnit.SECONDS);
            trips.put(trip.getId(), scheduledFuture);
        }
    }

    public Heartbeat generateRandomHeartbeat(Long id) {

        ThreadLocalRandom random = ThreadLocalRandom.current();
        HeartbeatType randomType = HeartbeatType.values()[random.nextInt(HeartbeatType.values().length)];
        Long randomDistance = random.nextLong(10);

        Heartbeat heartbeat = new Heartbeat(id, randomType, randomDistance);
        logger.info("Generating heartbeat: " + heartbeat);
        return heartbeat;
    }
}