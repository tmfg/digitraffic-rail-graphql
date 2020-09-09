package graphqlscope.graphql.fetchers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.dataloader.BatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphqlscope.graphql.entities.TrainId;
import graphqlscope.graphql.model.TrainLocationTO;
import graphqlscope.graphql.model.TrainTO;
import graphqlscope.graphql.repositories.TrainLocationRepository;
import graphqlscope.graphql.to.TrainLocationTOConverter;

@Component
public class TrainToTrainLocationsDataFetcher extends MyDataFetcher<TrainId, List<TrainLocationTO>> {

    @Autowired
    private DataFetcherFactory dataFetcherFactory;

    @Autowired
    private TrainLocationTOConverter trainLocationTOConverter;

    @Autowired
    private TrainLocationRepository trainLocationRepository;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "trainLocations";
    }

    @Override
    public DataFetcher<CompletableFuture<List<TrainLocationTO>>> createFetcher() {
        return dataFetcherFactory.createDataFetcher(getFieldName(), (TrainTO parent) -> new TrainId(parent.getTrainNumber().longValue(), parent.getDepartureDate()));
    }

    @Override
    public BatchLoader<TrainId, List<TrainLocationTO>> createLoader() {
        return dataFetcherFactory.createOneToManyDataLoader(parentIds -> trainLocationRepository.findAllByTrainIds(parentIds), child -> new TrainId(child.trainLocationId.trainNumber, child.trainLocationId.departureDate), trainLocationTOConverter::convert);
    }
}
