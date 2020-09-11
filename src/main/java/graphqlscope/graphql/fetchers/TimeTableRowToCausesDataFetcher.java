package graphqlscope.graphql.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.Cause;
import graphqlscope.graphql.entities.TimeTableRowId;
import graphqlscope.graphql.fetchers.base.OneToManyDataFetcher;
import graphqlscope.graphql.model.CauseTO;
import graphqlscope.graphql.model.TimeTableRowTO;
import graphqlscope.graphql.repositories.CauseRepository;
import graphqlscope.graphql.to.CauseTOConverter;

@Component
public class TimeTableRowToCausesDataFetcher extends OneToManyDataFetcher<TimeTableRowId, TimeTableRowTO, Cause, CauseTO> {
    @Autowired
    private CauseRepository causeRepository;

    @Autowired
    private CauseTOConverter causeTOConverter;

    @Override
    public String getTypeName() {
        return "TimeTableRow";
    }

    @Override
    public String getFieldName() {
        return "causes";
    }

    @Override
    public TimeTableRowId createKeyFromParent(TimeTableRowTO timeTableRow) {
        return new TimeTableRowId(timeTableRow.getId().longValue(), timeTableRow.getDepartureDate(), timeTableRow.getTrainNumber().longValue());
    }

    @Override
    public TimeTableRowId createKeyFromChild(Cause child) {
        return child.timeTableRowId;
    }

    @Override
    public CauseTO createChildTOToFromChild(Cause child) {
        return causeTOConverter.convert(child);
    }

    @Override
    public List<Cause> findChildrenByKeys(List<TimeTableRowId> keys) {
        return causeRepository.findAllByTimeTableRowIds(keys);
    }
}
