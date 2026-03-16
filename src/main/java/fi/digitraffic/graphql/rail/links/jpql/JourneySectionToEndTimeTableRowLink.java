package fi.digitraffic.graphql.rail.links.jpql;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TimeTableRowId;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TimeTableRowTOConverter;

@Component
public class JourneySectionToEndTimeTableRowLink extends JourneySectionToStartTimeTableRowLink {

    public JourneySectionToEndTimeTableRowLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                               final JpqlOrderByBuilder jpqlOrderByBuilder,
                                               @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                               final TimeTableRowTOConverter timeTableRowTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize, timeTableRowTOConverter);
    }

    @Override
    public String getFieldName() { return "endTimeTableRow"; }

    @Override
    public TimeTableRowId createKeyFromParent(final JourneySectionTO journeySectionTO) {
        Integer endId = journeySectionTO.getEndTimeTableRowId();
        if (endId == null) {
            endId = -1;
        }
        return new TimeTableRowId(endId, journeySectionTO.getDepartureDate(), journeySectionTO.getTrainNumber());
    }
}

