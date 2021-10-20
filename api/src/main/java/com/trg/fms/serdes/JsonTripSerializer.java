package com.trg.fms.serdes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.trg.fms.api.Trip;

import java.io.IOException;

public class JsonTripSerializer extends JsonSerializer<Trip> {

    @Override
    public void serialize(Trip trip, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", trip.getId());
        jsonGenerator.writeNumberField("driverId", trip.getDriverId());
        jsonGenerator.writeNumberField("carId", trip.getCarId());
        jsonGenerator.writeStringField("state", trip.getState().name());
        jsonGenerator.writeEndObject();
    }
}
