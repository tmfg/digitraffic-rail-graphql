package graphqlscope.graphql.fetchers;

import java.util.concurrent.CompletableFuture;

import org.dataloader.BatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphqlscope.graphql.model.TrainCategoryTO;
import graphqlscope.graphql.model.TrainTypeTO;
import graphqlscope.graphql.repositories.TrainCategoryRepository;
import graphqlscope.graphql.to.TrainCategoryTOConverter;

@Component
public class TrainTypeToTrainCategoryDataFetcher extends BaseDataFetcher<Long, TrainCategoryTO> {

    @Autowired
    private DataFetcherFactory dataFetcherFactory;

    @Autowired
    private TrainCategoryRepository trainCategoryRepository;

    @Autowired
    private TrainCategoryTOConverter trainCategoryTOConverter;

    @Override
    public String getTypeName() {
        return "TrainType";
    }

    @Override
    public String getFieldName() {
        return "trainCategory";
    }

    @Override
    public DataFetcher<CompletableFuture<TrainCategoryTO>> createFetcher() {
        return dataFetcherFactory.createDataFetcher(getFieldName(), (TrainTypeTO parent) -> parent.getTrainCategoryId().longValue());
    }

    @Override
    public BatchLoader<Long, TrainCategoryTO> createLoader() {
        return dataFetcherFactory.createOneToOneDataLoader(
                parentIds -> trainCategoryRepository.findAllById(parentIds),
                child -> child.id,
                trainCategoryTOConverter::convert);
    }
}
