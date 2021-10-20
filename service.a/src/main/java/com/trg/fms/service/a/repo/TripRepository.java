package com.trg.fms.service.a.repo;

import com.trg.fms.api.Trip;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface TripRepository extends R2dbcRepository<Trip, Long> {
}