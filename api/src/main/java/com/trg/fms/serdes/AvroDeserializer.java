package com.trg.fms.serdes;

import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;

public class AvroDeserializer<T> implements Deserializer<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AvroDeserializer.class);

  private final Class<T> target;

  public AvroDeserializer(Class<T> target) {
    this.target = target;
  }

  @Override
  public T deserialize(String topic, byte[] data) {
    try {
      T result = null;

      if (data != null) {
        LOGGER.debug("data='{}'", DatatypeConverter.printHexBinary(data));
        DatumReader<T> datumReader =
            new SpecificDatumReader<>(target);
        Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);

        result = datumReader.read(null, decoder);
        LOGGER.debug("deserialized data='{}'", result);
      }
      return result;
    } catch (Exception ex) {
      throw new SerializationException(
          "Can't deserialize data '" + Arrays.toString(data) + "' from topic '" + topic + "'", ex);
    }
  }
}