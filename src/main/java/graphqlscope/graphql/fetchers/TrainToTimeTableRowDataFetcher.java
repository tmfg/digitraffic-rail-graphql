package graphqlscope.graphql.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.TimeTableRow;
import graphqlscope.graphql.entities.TrainId;
import graphqlscope.graphql.fetchers.base.OneToManyDataFetcher;
import graphqlscope.graphql.model.TimeTableRowTO;
import graphqlscope.graphql.model.TrainTO;
import graphqlscope.graphql.repositories.TimeTableRowRepository;
import graphqlscope.graphql.to.TimeTableRowTOConverter;

@Component
public class TrainToTimeTableRowDataFetcher extends OneToManyDataFetcher<TrainId, TrainTO, TimeTableRow, TimeTableRowTO> {
    @Autowired
    private TimeTableRowRepository timeTableRowRepository;

    @Autowired
    private TimeTableRowTOConverter timeTableRowTOConverter;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "timeTableRows";
    }

    @Override
    public TrainId createKeyFromParent(TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber().longValue(), trainTO.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(TimeTableRow child) {
        return new TrainId(child.id.trainNumber, child.id.departureDate);
    }

    @Override
    public TimeTableRowTO createChildTOToFromChild(TimeTableRow child) {
        return timeTableRowTOConverter.convert(child);
    }

    @Override
    public List<TimeTableRow> findChildrenByKeys(List<TrainId> keys) {
        return timeTableRowRepository.findAllByTrainIds(keys);
    }
}
