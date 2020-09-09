package graphqlscope.graphql.fetchers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.dataloader.BatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphqlscope.graphql.entities.TrainId;
import graphqlscope.graphql.model.CompositionTO;
import graphqlscope.graphql.model.JourneySectionTO;
import graphqlscope.graphql.repositories.JourneySectionRepository;
import graphqlscope.graphql.to.JourneySectionTOConverter;

@Component
public class CompositionToJourneySectionsDataFetcher extends MyDataFetcher<TrainId, List<JourneySectionTO>> {

    @Autowired
    private DataFetcherFactory dataFetcherFactory;

    @Autowired
    private JourneySectionRepository journeySectionRepository;

    @Autowired
    private JourneySectionTOConverter journeySectionTOConverter;

    @Override
    public String getTypeName() {
        return "Composition";
    }

    @Override
    public String getFieldName() {
        return "journeySections";
    }

    @Override
    public DataFetcher<CompletableFuture<List<JourneySectionTO>>> createFetcher() {
        return dataFetcherFactory.createDataFetcher(getFieldName(), (CompositionTO parent) -> new TrainId(parent.getTrainNumber().longValue(), parent.getDepartureDate()));
    }

    @Override
    public BatchLoader<TrainId, List<JourneySectionTO>> createLoader() {
        return dataFetcherFactory.createOneToManyDataLoader(parentIds -> journeySectionRepository.findAllByTrainIds(parentIds), child -> new TrainId(child.trainId.trainNumber, child.trainId.departureDate), journeySectionTOConverter::convert);
    }
}
