package graphqlscope.graphql.fetchers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.dataloader.BatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphqlscope.graphql.entities.TrainId;
import graphqlscope.graphql.model.CompositionTO;
import graphqlscope.graphql.model.TrainTO;
import graphqlscope.graphql.repositories.CompositionRepository;
import graphqlscope.graphql.to.CompositionTOConverter;

@Component
public class TrainToCompositionsDataFetcher extends BaseDataFetcher<TrainId, List<CompositionTO>> {

    @Autowired
    private DataFetcherFactory dataFetcherFactory;

    @Autowired
    private CompositionRepository compositionRepository;

    @Autowired
    private CompositionTOConverter compositionTOConverter;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "compositions";
    }

    @Override
    public DataFetcher<CompletableFuture<List<CompositionTO>>> createFetcher() {
        return dataFetcherFactory.createDataFetcher(getFieldName(), (TrainTO parent) -> new TrainId(parent.getTrainNumber().longValue(), parent.getDepartureDate()));
    }

    @Override
    public BatchLoader<TrainId, List<CompositionTO>> createLoader() {
        return dataFetcherFactory.createOneToManyDataLoader(parentIds -> compositionRepository.findAllByTrainIds(parentIds), child -> new TrainId(child.id.trainNumber, child.id.departureDate), compositionTOConverter::convert);
    }
}
