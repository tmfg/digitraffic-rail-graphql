package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.TimeTableRowId;
import fi.digitraffic.graphql.rail.links.base.OneToOneDataFetcher;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.repositories.TimeTableRowRepository;
import fi.digitraffic.graphql.rail.to.TimeTableRowTOConverter;

@Component
public class JourneySectionToStartTimeTableRowDataFetcher extends OneToOneDataFetcher<TimeTableRowId, JourneySectionTO, TimeTableRow, TimeTableRowTO> {
    @Autowired
    private TimeTableRowRepository timeTableRowRepository;

    @Autowired
    private TimeTableRowTOConverter timeTableRowTOConverter;

    @Override
    public String getTypeName() {
        return "JourneySection";
    }

    @Override
    public String getFieldName() {
        return "startTimeTableRow";
    }

    @Override
    public TimeTableRowId createKeyFromParent(JourneySectionTO journeySectionTO) {
        return new TimeTableRowId(journeySectionTO.getBeginTimeTableRowId(), journeySectionTO.getDepartureDate(), journeySectionTO.getTrainNumber().longValue());
    }

    @Override
    public TimeTableRowId createKeyFromChild(TimeTableRow child) {
        return child.id;
    }

    @Override
    public TimeTableRowTO createChildTOToFromChild(TimeTableRow child) {
        return timeTableRowTOConverter.convert(child);
    }

    @Override
    public List<TimeTableRow> findChildrenByKeys(List<TimeTableRowId> keys) {
        return timeTableRowRepository.findAllById(keys);
    }

}
