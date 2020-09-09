package graphqlscope.graphql.fetchers;

import java.util.concurrent.CompletableFuture;

import org.dataloader.BatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphqlscope.graphql.model.TrainTO;
import graphqlscope.graphql.model.TrainTypeTO;
import graphqlscope.graphql.repositories.TrainTypeRepository;
import graphqlscope.graphql.to.TrainTypeTOConverter;

@Component
public class TrainToTrainTypeDataFetcher extends BaseDataFetcher<Long, TrainTypeTO> {

    @Autowired
    private DataFetcherFactory dataFetcherFactory;

    @Autowired
    private TrainTypeTOConverter trainTypeTOConverter;

    @Autowired
    private TrainTypeRepository trainTypeRepository;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "trainType";
    }

    @Override
    public DataFetcher<CompletableFuture<TrainTypeTO>> createFetcher() {
        return dataFetcherFactory.createDataFetcher(getFieldName(), (TrainTO parent) -> parent.getTrainTypeId().longValue());
    }

    @Override
    public BatchLoader<Long, TrainTypeTO> createLoader() {
        return dataFetcherFactory.createOneToOneDataLoader(
                parentIds -> trainTypeRepository.findAllById(parentIds),
                child -> child.id,
                trainTypeTOConverter::convert);
    }
}
