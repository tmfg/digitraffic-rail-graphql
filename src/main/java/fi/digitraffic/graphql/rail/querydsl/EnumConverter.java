package fi.digitraffic.graphql.rail.querydsl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import fi.digitraffic.graphql.rail.entities.StationTypeEnum;
import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessageTypeEnum;
import jakarta.annotation.PostConstruct;

@Service
public class EnumConverter {

    private final Map<String, Enum<?>> enumValues = new HashMap<>();

    @PostConstruct
    public void setup() {
        for (final Enum<?>[] values : List.of(
                TimeTableRow.EstimateSourceEnum.values(),
                Train.TimetableType.values(),
                TimeTableRow.TimeTableRowType.values(),
                StationTypeEnum.values(),
                TrainTrackingMessageTypeEnum.values())) {
            for (final Enum<?> v : values) {
                enumValues.put(v.name(), v);
            }
        }
    }

    public Object convert(final Object value) {
        if (value instanceof final String s && enumValues.containsKey(s)) {
            return enumValues.get(s);
        }
        return value;
    }
}

