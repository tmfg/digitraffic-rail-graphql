package fi.digitraffic.graphql.rail.filters;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.model.TimeTableRowFilterTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;

@Component
public class TimeTableRowFilter extends BaseFilter<TimeTableRowTO, TimeTableRowFilterTO> {

    public boolean isFiltered(TimeTableRowTO entity, TimeTableRowFilterTO filter) {
        if (this.filterByAnd(filter.getAnd(), entity)) return true;
        if (this.filterByOr(filter.getOr(), entity)) return true;

        if (this.isChildFiltered(entity.getCommercialTrack(), filter.getCommercialTrack())) return true;
        if (this.isChildFiltered(entity.getType(), filter.getType())) return true;
        if (this.isChildFiltered(entity.getStation(), filter.getStation())) return true;

        return false;
    }

    public Class<TimeTableRowFilterTO> getFilterTOType() {
        return TimeTableRowFilterTO.class;
    }

    public Class<TimeTableRowTO> getEntityTOType() {
        return TimeTableRowTO.class;
    }
}
