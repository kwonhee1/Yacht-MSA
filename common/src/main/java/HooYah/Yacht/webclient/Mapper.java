package HooYah.Yacht.webclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Map;

public class Mapper {
    private static ObjectMapper objectMapper;

    public static void setObjectMapper(TimeZone timeZone) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setTimeZone(timeZone.getTimeZone());
    }

    public static String toString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObject(String value, Class<T> clazz) {
        try {
            return objectMapper.readValue(value, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convert(Object value, Class<T> clazz) {
        return objectMapper.convertValue(value, clazz);
    }

    public static <T> T convert(Object value, TypeReference<T> typeReference) {
        return objectMapper.convertValue(value, typeReference);
    }

}