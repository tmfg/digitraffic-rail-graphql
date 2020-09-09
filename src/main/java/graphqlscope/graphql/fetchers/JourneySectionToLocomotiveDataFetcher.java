package graphqlscope.graphql.fetchers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.dataloader.BatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphqlscope.graphql.model.JourneySectionTO;
import graphqlscope.graphql.model.LocomotiveTO;
import graphqlscope.graphql.repositories.LocomotiveRepository;
import graphqlscope.graphql.to.LocomotiveTOConverter;

@Component
public class JourneySectionToLocomotiveDataFetcher extends BaseDataFetcher<Long, List<LocomotiveTO>> {

    @Autowired
    private DataFetcherFactory dataFetcherFactory;

    @Autowired
    private LocomotiveRepository locomotiveRepository;

    @Autowired
    private LocomotiveTOConverter locomotiveTOConverter;

    @Override
    public String getTypeName() {
        return "JourneySection";
    }

    @Override
    public String getFieldName() {
        return "locomotives";
    }

    @Override
    public DataFetcher<CompletableFuture<List<LocomotiveTO>>> createFetcher() {
        return dataFetcherFactory.createDataFetcher(getFieldName(), (JourneySectionTO parent) -> parent.getId().longValue());
    }

    @Override
    public BatchLoader<Long, List<LocomotiveTO>> createLoader() {
        return dataFetcherFactory.createOneToManyDataLoader(parentIds -> locomotiveRepository.findAllByJourneySectionIds(parentIds), child -> child.journeysectionId, locomotiveTOConverter::convert);
    }
}
