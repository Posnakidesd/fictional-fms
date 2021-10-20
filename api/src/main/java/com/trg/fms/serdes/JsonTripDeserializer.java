package com.trg.fms.serdes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.trg.fms.api.Trip;
import com.trg.fms.api.TripState;

import java.io.IOException;

public class JsonTripDeserializer extends JsonDeserializer<Trip> {

    @Override
    public Trip deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        JsonNode fieldNode;
        fieldNode = node.get("id");
        Long id = fieldNode != null ? fieldNode.longValue() : null;
        fieldNode = node.get("carId");
        Long carId = fieldNode != null && !fieldNode.isNull() ? fieldNode.longValue() : null;
        fieldNode = node.get("driverId");
        Long driverId = fieldNode != null && !fieldNode.isNull() ? fieldNode.longValue() : null;
        fieldNode = node.get("state");
        TripState state = fieldNode != null && !fieldNode.isNull() ? TripState.valueOf(TripState.class, fieldNode.asText()) : TripState.STOP;

        return new Trip(id, driverId, carId, state);
    }
}
