package com.trg.fms.service.a.controllers;

import com.trg.fms.api.Trip;
import com.trg.fms.api.TripState;
import com.trg.fms.service.a.repo.CarRepository;
import com.trg.fms.service.a.repo.DriverRepository;
import com.trg.fms.service.a.repo.TripRepository;
import com.trg.fms.service.a.service.KafkaTripProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(TripController.class)
public class TripControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private TripRepository tripRepository;

    @MockBean
    KafkaTripProducerService kafkaTripProducerService;

    @MockBean
    DriverRepository driverRepository;
    @MockBean
    CarRepository carRepository;

    @Test
    public void testGetAllTrips() {

        Trip trip1 = new Trip(1L, 1L, 1L, TripState.START);
        Trip trip2 = new Trip(2L, 1L, 1L, TripState.START);

        Flux<Trip> tripFlux = Flux.just(trip1, trip2);

        when(tripRepository.findAll()).thenReturn(tripFlux);

        webTestClient.get()
                .uri("/api/v1/trips")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Trip.class)
                .value(trips -> {
                    assertThat(trips.size(), equalTo(2));
                });
    }

    @Test
    public void testGetTrip() {

        Trip trip = new Trip(1L, 1L, 1L, TripState.START);

        Mono<Trip> tripMono = Mono.just(trip);

        when(tripRepository.findById(1L)).thenReturn(tripMono);

        webTestClient.get()
                .uri("/api/v1/trip/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Trip.class)
                .value(Trip::getDriverId, equalTo(1L));
    }

    @Test
    public void testCreateTripWithNonNullTripId() {

        Trip trip = new Trip(1L, 1L, 1L, TripState.START);

        webTestClient.post()
                .uri("/api/v1/trip")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(trip), Trip.class)
                .exchange()
                .expectStatus().isBadRequest();
    }
}