package graphqlscope.graphql.fetchers;

import org.dataloader.BatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphqlscope.graphql.model.OperatorTO;
import graphqlscope.graphql.model.TrainTO;
import graphqlscope.graphql.repositories.OperatorRepository;

@Component
public class TrainToOperatorDataFetcher extends MyDataFetcher {

    @Autowired
    private DataFetcherFactory dataFetcherFactory;

    @Autowired
    private OperatorRepository operatorRepository;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "operator";
    }

    @Override
    public DataFetcher createFetcher() {
        return dataFetcherFactory.createDataFetcher(getFieldName(), (TrainTO parent) -> parent.getOperatorShortCode());
    }

    @Override
    public BatchLoader<String, OperatorTO> createLoader() {
        return dataFetcherFactory.createOneToOneDataLoader(
                parentIds -> operatorRepository.findByOperatorShortCodeIn(parentIds),
                child -> child.operatorShortCode,
                child -> new OperatorTO(child.operatorName, child.operatorShortCode, child.operatorUicCode));
    }
}
