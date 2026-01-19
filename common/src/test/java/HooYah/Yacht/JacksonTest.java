package HooYah.Yacht;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.TimeZone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class JacksonTest {

    @Test
    public void SerializeOffsetDateTimeTest() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // should set TimeZone
        mapper.registerModule(new JavaTimeModule()); // should register module to serialize OffsetDateTime!

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                OffsetDateTime offsetDateTime = OffsetDateTime.now();
                String serialized = mapper.writeValueAsString(new Data(offsetDateTime));

                Data deSerialized = mapper.readValue(serialized, Data.class); // must deSerialized to Object , not Map!

                Assertions.assertEquals(offsetDateTime, deSerialized.getDate());
            }
        });
    }

    static class Data {
        OffsetDateTime date;
        public Data() {}
        public Data(OffsetDateTime date) {
            this.date = date;
        }
        public OffsetDateTime getDate() {
            return date;
        }
    }

    @Test
    public void deSerializeWithMap() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        String serialized = mapper.writeValueAsString(new Data(offsetDateTime));

        // deSerialize to Map
        Map deSerialized = mapper.readValue(serialized, Map.class);

        Assertions.assertNotEquals(offsetDateTime, deSerialized.get("date"));
        /*
        org.opentest4j.AssertionFailedError:
            Expected :2026-01-19T15:15:06.464314100+09:00
            Actual   :1.768803306464314E9 // ObjectMapper가 값을 OffsetDateTime으로 read 하지 않고 String그대로 읽어버림
         */
    }

}
