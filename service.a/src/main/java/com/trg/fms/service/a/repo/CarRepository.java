package com.trg.fms.service.a.repo;

import com.trg.fms.api.Car;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CarRepository extends R2dbcRepository<Car, Long> {
}