package graphqlscope.graphql.fetchers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.dataloader.BatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphqlscope.graphql.entities.TrainId;
import graphqlscope.graphql.model.TimeTableRowTO;
import graphqlscope.graphql.model.TrainTO;
import graphqlscope.graphql.repositories.TimeTableRowRepository;
import graphqlscope.graphql.to.TimeTableRowTOConverter;

@Component
public class TrainToTimeTableRowDataFetcher extends MyDataFetcher<TrainId, List<TimeTableRowTO>> {

    @Autowired
    private DataFetcherFactory dataFetcherFactory;

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
    public DataFetcher<CompletableFuture<List<TimeTableRowTO>>> createFetcher() {
        return dataFetcherFactory.createDataFetcher(getFieldName(), (TrainTO parent) -> new TrainId(parent.getTrainNumber().longValue(), parent.getDepartureDate()));
    }

    @Override
    public BatchLoader<TrainId, List<TimeTableRowTO>> createLoader() {
        return dataFetcherFactory.createOneToManyDataLoader(parentIds -> timeTableRowRepository.findAllByTrainIds(parentIds), child -> new TrainId(child.id.trainNumber, child.id.departureDate), timeTableRowTOConverter::convert);
    }
}
