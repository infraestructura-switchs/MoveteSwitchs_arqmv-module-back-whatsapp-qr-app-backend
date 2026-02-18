package com.restaurante.bot.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Log4j2
public class DateDeserializerTimestamp implements JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            String dateString = json.getAsJsonPrimitive().getAsString();
            try {
                OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateString, formatter);
                return offsetDateTime.toLocalDateTime(); // Convertimos a LocalDateTime
            } catch (Exception e) {
                log.debug("Error parsing date: " + dateString, e);
                throw new JsonParseException("Unsupported date format: " + dateString);
            }
        }
        throw new JsonParseException("Unsupported date format: " + json);
    }
}
