package fi.digitraffic.graphql.rail.filters;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.model.TimeTableRowFilterTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;

@Component
public class TimeTableRowFilter extends BaseFilter<TimeTableRowTO, TimeTableRowFilterTO> {
    public Class<TimeTableRowFilterTO> getFilterTOType() {
        return TimeTableRowFilterTO.class;
    }

    public Class<TimeTableRowTO> getEntityTOType() {
        return TimeTableRowTO.class;
    }
}
