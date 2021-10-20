package com.trg.fms.service.a.controllers;

import com.trg.fms.api.Trip;
import com.trg.fms.api.TripState;
import com.trg.fms.service.a.repo.CarRepository;
import com.trg.fms.service.a.repo.DriverRepository;
import com.trg.fms.service.a.service.KafkaTripProducerService;
import com.trg.fms.service.a.repo.TripRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1")
public class TripController {

    Logger logger = LoggerFactory.getLogger(TripController.class);

    @Autowired
    KafkaTripProducerService kafkaTripProducerService;

    @Autowired
    TripRepository tripRepository;
    @Autowired
    DriverRepository driverRepository;
    @Autowired
    CarRepository carRepository;

    @GetMapping("/trips")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    @GetMapping("/trip/{id}")
    public Mono<ResponseEntity<Trip>> getTrip(@PathVariable("id") Long id) {

        return tripRepository.findById(id)
                .map(trip -> new ResponseEntity<>(trip, HttpStatus.OK))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/trip", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<String>> createTrip(@RequestBody Trip trip, UriComponentsBuilder builder) {

        logger.info("Creating trip : "  + trip);
        //We use post to create new records only
        if (trip.getId() != null) {
            logger.warn("Cannot create trip with existing id");
            return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }

        Mono<Trip> tripFromKafkaMono = Mono.zip(driverRepository.existsById(trip.getDriverId()), carRepository.existsById(trip.getCarId()))
                .map(tuple -> tuple.getT1() && tuple.getT2())
                .flatMap(carDriverExist -> {
                    if (carDriverExist) {
                        Mono<Trip> tripWithIdMono = tripRepository.save(trip);
                        return tripWithIdMono.flatMap(tripWithId -> kafkaTripProducerService.send(tripWithId)
                                .flatMap(longTripSendResult -> Mono.just(longTripSendResult.getProducerRecord().value())));
                    } else {
                        return Mono.empty();
                    }
                });

        return tripFromKafkaMono.map(tripFromKafka -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(builder.path("/trip/{id}").buildAndExpand(tripFromKafka.getId()).toUri());
            return new ResponseEntity<>("Trip Created. TripID: " + tripFromKafka, headers, HttpStatus.CREATED);
        }).defaultIfEmpty(new ResponseEntity<>("Driver ID Or Car ID NOT FOUND", HttpStatus.BAD_REQUEST));
    }

    @PutMapping(path = "/trip", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<Trip>> updateTrip(@RequestBody Trip trip) {

        return tripRepository.existsById(trip.getId()).flatMap(exists -> {
            if (exists) {
                return tripRepository.save(trip)
                        .flatMap(tripWithId -> kafkaTripProducerService.send(tripWithId)
                                .flatMap(longTripSendResult -> Mono.just(longTripSendResult.getProducerRecord().value())))
                        .map(tripFromKafka -> new ResponseEntity<>(tripFromKafka, HttpStatus.OK));
            } else {
                return Mono.just(ResponseEntity.notFound().build());
            }
        });
    }

    @DeleteMapping("/trip/{id}")
    public Mono<ResponseEntity<String>> deleteTripById(@PathVariable("id") Long id) {

        return tripRepository.deleteById(id)
                .flatMap(unused -> {
                    Trip deleteTrip = new Trip();
                    deleteTrip.setId(id);
                    deleteTrip.setState(TripState.STOP);
                    return kafkaTripProducerService.send(deleteTrip)
                            .flatMap(longTripSendResult -> Mono.just(longTripSendResult.getProducerRecord().value()));
                })
                .map(tripFromKafka -> new ResponseEntity<>("Trip deleted", HttpStatus.OK));
    }
}
