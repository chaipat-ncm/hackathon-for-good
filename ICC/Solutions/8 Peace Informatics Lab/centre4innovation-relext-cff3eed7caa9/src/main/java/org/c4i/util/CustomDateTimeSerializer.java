package org.c4i.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Enforcing a single format when showing JSON date times.
 * See: http://stackoverflow.com/questions/3269459/how-to-serialize-joda-datetime-with-jackson-json-processer
 * @version 11-8-16
 * @author Arvid Halma
 */
public class CustomDateTimeSerializer extends JsonSerializer<DateTime> {

    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ssZZ");

    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializerProvider arg2) throws IOException, JsonProcessingException {
        gen.writeString(formatter.print(value));
    }
}
