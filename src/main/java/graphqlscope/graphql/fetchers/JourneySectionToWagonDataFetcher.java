package graphqlscope.graphql.fetchers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.dataloader.BatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphqlscope.graphql.model.JourneySectionTO;
import graphqlscope.graphql.model.WagonTO;
import graphqlscope.graphql.repositories.WagonRepository;
import graphqlscope.graphql.to.WagonTOConverter;

@Component
public class JourneySectionToWagonDataFetcher extends MyDataFetcher<Long, List<WagonTO>> {

    @Autowired
    private DataFetcherFactory dataFetcherFactory;

    @Autowired
    private WagonRepository wagonRepository;

    @Autowired
    private WagonTOConverter wagonTOConverter;

    @Override
    public String getTypeName() {
        return "JourneySection";
    }

    @Override
    public String getFieldName() {
        return "wagons";
    }

    @Override
    public DataFetcher<CompletableFuture<List<WagonTO>>> createFetcher() {
        return dataFetcherFactory.createDataFetcher(getFieldName(), (JourneySectionTO parent) -> parent.getId().longValue());
    }

    @Override
    public BatchLoader<Long, List<WagonTO>> createLoader() {
        return dataFetcherFactory.createOneToManyDataLoader(parentIds -> wagonRepository.findAllByJourneySectionIds(parentIds), child -> child.journeysectionId, wagonTOConverter::convert);
    }
}
