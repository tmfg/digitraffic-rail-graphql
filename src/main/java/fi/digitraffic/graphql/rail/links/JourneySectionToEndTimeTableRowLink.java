package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.stereotype.Component;

import com.querydsl.core.types.Expression;
import fi.digitraffic.graphql.rail.entities.QJourneySection;
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
        Integer endTimeTableRowId = journeySectionTO.getEndTimeTableRowId();
        if (endTimeTableRowId == null) {
            endTimeTableRowId = -1;
        }
        return new TimeTableRowId(endTimeTableRowId, journeySectionTO.getDepartureDate(), journeySectionTO.getTrainNumber());
    }

    @Override
    public List<Expression<?>> columnsNeededFromParentTable() {
        return List.of(QJourneySection.journeySection.saapAttapId, QJourneySection.journeySection.trainId);
    }
}
