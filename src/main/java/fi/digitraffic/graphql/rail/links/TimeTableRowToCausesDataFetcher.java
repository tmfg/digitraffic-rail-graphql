package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Cause;
import fi.digitraffic.graphql.rail.entities.TimeTableRowId;
import fi.digitraffic.graphql.rail.links.base.OneToManyDataFetcher;
import fi.digitraffic.graphql.rail.model.CauseTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.repositories.CauseRepository;
import fi.digitraffic.graphql.rail.to.CauseTOConverter;

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
