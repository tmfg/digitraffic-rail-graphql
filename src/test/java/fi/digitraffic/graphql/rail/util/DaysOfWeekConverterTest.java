package fi.digitraffic.graphql.rail.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class DaysOfWeekConverterTest {
    @Test
    public void conversionFromBitsWorks() {
        final DaysOfWeekConverter converter = new DaysOfWeekConverter();

        final String weekDayBits = "0110000";
        final Integer bitsAsNumber = Integer.parseInt(weekDayBits);

        final List<String> result = converter.convertToEntityAttribute(bitsAsNumber);

        assertEquals(2, result.size());
        assertTrue(result.contains("TUESDAY"));
        assertTrue(result.contains("WEDNESDAY"));
    }
}
