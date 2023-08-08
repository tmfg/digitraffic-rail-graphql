package fi.digitraffic.graphql.rail.links;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TimeTableRowId;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;

@Component
public class JourneySectionToEndTimeTableRowLink extends JourneySectionToStartTimeTableRowLink {

    @Override
    public String getFieldName() {
        return "endTimeTableRow";
    }

    @Override
    public TimeTableRowId createKeyFromParent(final JourneySectionTO journeySectionTO) {
        Long endTimeTableRowId = journeySectionTO.getEndTimeTableRowId();
        if (endTimeTableRowId == null) {
            endTimeTableRowId = -1L;
        }
        return new TimeTableRowId(endTimeTableRowId, journeySectionTO.getDepartureDate(), journeySectionTO.getTrainNumber());
    }
}
