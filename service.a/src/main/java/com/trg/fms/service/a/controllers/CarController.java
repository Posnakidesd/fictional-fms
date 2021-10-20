package com.trg.fms.service.a.controllers;

import com.trg.fms.api.Car;
import com.trg.fms.service.a.repo.CarRepository;
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
public class CarController {

    @Autowired
    CarRepository carRepository;

    @GetMapping("/cars")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Car> getAllCars() {
        return carRepository.findAll();
    }

    @GetMapping("/car/{id}")
    public Mono<ResponseEntity<Car>> getCar(@PathVariable("id") Long id) {
        return carRepository.findById(id)
                .map(car -> new ResponseEntity<>(car, HttpStatus.OK))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/car", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<Void>> createCar(@RequestBody Car car, UriComponentsBuilder builder) {

        //We use post to create new records only
        if(car.getId() != null) {
            return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }
        Mono<Car> carWithIdMono = carRepository.save(car);

        return carWithIdMono.map(carWithId -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(builder.path("/car/{id}").buildAndExpand(carWithId.getId()).toUri());
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        });
    }

    @PutMapping(path = "/car", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<Car>> updateCar(@RequestBody Car car) {

        return carRepository.existsById(car.getId()).flatMap(exists -> {
            if (exists) {
                return carRepository.save(car)
                        .map(carFromDb -> new ResponseEntity<>(carFromDb, HttpStatus.OK));
            } else {
                return Mono.just(ResponseEntity.notFound().build());
            }
        });
    }

    @DeleteMapping("/car/{id}")
    public Mono<ResponseEntity<Void>> deleteCarById(@PathVariable("id") Long id) {
        return carRepository.deleteById(id).map(unused -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
