package graphqlscope.graphql.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.TimeTableRow;
import graphqlscope.graphql.entities.TimeTableRowId;
import graphqlscope.graphql.fetchers.base.OneToOneDataFetcher;
import graphqlscope.graphql.model.JourneySectionTO;
import graphqlscope.graphql.model.TimeTableRowTO;
import graphqlscope.graphql.repositories.TimeTableRowRepository;
import graphqlscope.graphql.to.TimeTableRowTOConverter;

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
