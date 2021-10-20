package com.trg.fms.service.a.repo;

import com.trg.fms.api.Driver;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface DriverRepository extends R2dbcRepository<Driver, Long> {
}