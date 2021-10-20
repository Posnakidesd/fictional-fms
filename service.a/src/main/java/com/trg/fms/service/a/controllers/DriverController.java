package com.trg.fms.service.a.controllers;

import com.trg.fms.api.Driver;
import com.trg.fms.service.a.repo.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1")
public class DriverController {

    @Autowired
    DriverRepository driverRepository;

    @GetMapping("/drivers")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @GetMapping("/driver/{id}")
    public Mono<ResponseEntity<Driver>> getDriver(@PathVariable("id") Long id) {
        return driverRepository.findById(id)
                .map(driver -> new ResponseEntity<>(driver, HttpStatus.OK))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/driver", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<Void>> createDriver(@RequestBody Driver driver, UriComponentsBuilder builder) {

        //We use post to create new records only
        if(driver.getId() != null) {
            return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }
        Mono<Driver> driverWithIdMono = driverRepository.save(driver);

        return driverWithIdMono.map(driverWithId -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(builder.path("/driver/{id}").buildAndExpand(driverWithId.getId()).toUri());
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        });
    }

    @PutMapping(path = "/driver", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<Driver>> updateDriver(@RequestBody Driver driver) {

        return driverRepository.existsById(driver.getId()).flatMap(exists -> {
            if (exists) {
                return driverRepository.save(driver)
                        .map(driverFromDb -> new ResponseEntity<>(driverFromDb, HttpStatus.OK));
            } else {
                return Mono.just(ResponseEntity.notFound().build());
            }
        });
    }

    @DeleteMapping("/driver/{id}")
    public Mono<ResponseEntity<Void>> deleteDriverById(@PathVariable("id") Long id) {
        return driverRepository.deleteById(id).map(unused -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
