package com.trg.fms.service.c.repo;

import com.trg.fms.api.Penalty;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface PenaltyRepository extends R2dbcRepository<Penalty, Long> {

    @Query("SELECT * FROM penalty WHERE driver_id = $1")
    Mono<Penalty> findByDriverId(Long driverId);

}