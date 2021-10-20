package com.trg.fms.service.c.serde;

import com.trg.fms.service.c.streams.aggregates.PenaltyAggregate;
import org.apache.kafka.common.serialization.Serdes;

import java.util.HashMap;
import java.util.Map;
public class PenaltyAggregateSerde extends Serdes.WrapperSerde<PenaltyAggregate> {

        public PenaltyAggregateSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
            this.configure();
        }
        private void configure() {
            Map<String, Object> serdeConfigs = new HashMap<>();
            serdeConfigs.put("class.name", PenaltyAggregate.class);
            super.configure(serdeConfigs, false);
        }
    }
