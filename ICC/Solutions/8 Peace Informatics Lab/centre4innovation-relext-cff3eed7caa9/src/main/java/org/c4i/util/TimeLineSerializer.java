package org.c4i.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @author Arvid Halma
 * @version 16-2-17
 */
public class TimeLineSerializer extends StdSerializer<TimeLine> {

    public TimeLineSerializer() {
        this(null);
    }

    public TimeLineSerializer(Class<TimeLine> t) {
        super(t);
    }

    @Override
    public void serialize(
            TimeLine timeline, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartArray();
        for (Object value : timeline) {
            jgen.writeObject(value);
        }
        jgen.writeEndArray();
    }
}