package fi.digitraffic.graphql.rail.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DaysOfWeekConverter implements AttributeConverter<List<String>, Integer> {

    private static final String[] DAYS = { "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY" };

    @Override
    public Integer convertToDatabaseColumn(final List<String> dayList) {
        int days = 0;
        for (int i = 0; i < DAYS.length; i++) {
            if (dayList.contains(DAYS[i])) {
                days |= 1 << i;
            }
        }
        return days;
    }

    @Override
    public List<String> convertToEntityAttribute(final Integer daysBits) {
        final List<String> days = new ArrayList<>();
        for (int i = 0; i < DAYS.length; i++) {
            if ((daysBits & (1 << i)) != 0) {
                days.add(DAYS[DAYS.length - i - 1]);
            }
        }
        Collections.reverse(days);
        return days;
    }
}