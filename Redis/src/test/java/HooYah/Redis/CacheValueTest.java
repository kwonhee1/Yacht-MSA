package HooYah.Redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class CacheValueTest {

    @Test
    @DisplayName("ofSaved handles null, empty, and valid strings correctly for status checks")
    void ofSaved_statusChecks() {
        // Case 1: null input (represents un-known value from cache)
        CacheValue nullInput = CacheValue.ofSaved(null);
        assertThat(nullInput.hasValue()).isFalse(); // No concrete value
        assertThat(nullInput.isNull()).isFalse();   // Not explicitly NULL

        // Case 2: empty string input (represents un-known value from cache)
        CacheValue emptyInput = CacheValue.ofSaved("");
        assertThat(emptyInput.hasValue()).isFalse(); // No concrete value
        assertThat(emptyInput.isNull()).isFalse();   // Not explicitly NULL

        // Case 3: "NULL" string input (represents explicitly stored null value)
        CacheValue nullStringInput = CacheValue.ofSaved(CacheValue.NULL);
        assertThat(nullStringInput.hasValue()).isTrue(); // It has a value (which is null)
        assertThat(nullStringInput.isNull()).isTrue();   // It is explicitly NULL

        // Case 4: Valid serialized string input (represents an existing value)
        String validData = "{\"key\":\"value\"}";
        CacheValue validStringInput = CacheValue.ofSaved(validData);
        assertThat(validStringInput.hasValue()).isTrue(); // It has a concrete value
        assertThat(validStringInput.isNull()).isFalse();  // It is not NULL
    }

    @Test
    @DisplayName("ofSource handles null and valid strings correctly for status checks")
    void ofSource_statusChecks() {
        // Case 1: null object input (represents an explicit null to be stored)
        CacheValue nullObject = CacheValue.ofSource(null);
        assertThat(nullObject.hasValue()).isTrue(); // It has a value (which is the "NULL" marker)
        assertThat(nullObject.isNull()).isTrue();   // It is explicitly NULL

        // Case 2: empty string input (represents an empty string object to be stored)
        String emptyData = "";
        CacheValue emptyStringObject = CacheValue.ofSource(emptyData);
        assertThat(emptyStringObject.hasValue()).isTrue(); // It has a concrete value
        assertThat(emptyStringObject.isNull()).isFalse();  // It is not NULL

        // Case 3: Valid serialized string input (represents an object to be stored)
        String validData = "{\"id\":123}";
        CacheValue validObject = CacheValue.ofSource(validData);
        assertThat(validObject.hasValue()).isTrue(); // It has a concrete value
        assertThat(validObject.isNull()).isFalse();  // It is not NULL
    }
}