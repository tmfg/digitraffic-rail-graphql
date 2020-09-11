package graphqlscope.graphql.fetchers;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.TimeTableRowId;
import graphqlscope.graphql.model.JourneySectionTO;

@Component
public class JourneySectionToEndTimeTableRowDataFetcher extends JourneySectionToStartTimeTableRowDataFetcher {

    @Override
    public String getFieldName() {
        return "endTimeTableRow";
    }

    @Override
    public TimeTableRowId createKeyFromParent(JourneySectionTO journeySectionTO) {
        return new TimeTableRowId(journeySectionTO.getEndTimeTableRowId(), journeySectionTO.getDepartureDate(), journeySectionTO.getTrainNumber().longValue());
    }
}
