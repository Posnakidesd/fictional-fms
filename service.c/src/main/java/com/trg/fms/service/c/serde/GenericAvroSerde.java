package com.trg.fms.service.c.serde;

import com.trg.fms.api.Heartbeat;
import com.trg.fms.serdes.AvroDeserializer;
import com.trg.fms.serdes.AvroSerializer;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.*;

public class GenericAvroSerde<T extends SpecificRecordBase> extends Serdes.WrapperSerde<T> {

    public GenericAvroSerde(Class<T> target) {
        super(new AvroSerializer<>(), new AvroDeserializer<>(target));
    }
}

