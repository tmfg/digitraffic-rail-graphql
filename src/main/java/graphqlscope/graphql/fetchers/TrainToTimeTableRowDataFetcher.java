package graphqlscope.graphql.fetchers;

import java.util.List;

import org.dataloader.BatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphqlscope.graphql.entities.TrainId;
import graphqlscope.graphql.model.TimeTableRowTO;
import graphqlscope.graphql.model.TrainTO;
import graphqlscope.graphql.repositories.TimeTableRowRepository;

@Component
public class TrainToTimeTableRowDataFetcher extends MyDataFetcher {

    @Autowired
    private DataFetcherFactory dataFetcherFactory;

    @Autowired
    private TimeTableRowRepository timeTableRowRepository;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "timeTableRows";
    }

    @Override
    public DataFetcher createFetcher() {
        return dataFetcherFactory.createDataFetcher(getFieldName(), (TrainTO parent) -> new TrainId(parent.getTrainNumber().longValue(), parent.getDepartureDate()));
    }

    @Override
    public BatchLoader<TrainId, List<TimeTableRowTO>> createLoader() {
        return dataFetcherFactory.createDataLoader(
                parentIds -> timeTableRowRepository.findAllByTrainIds(parentIds),
                child -> new TrainId(child.id.trainNumber, child.id.departureDate),
                child -> new TimeTableRowTO("a", 1, "b", "c", true, true, "d", true, child.scheduledTime, null, child.id.attapId.intValue(), child.id.trainNumber.intValue(), child.id.departureDate));
    }
}
