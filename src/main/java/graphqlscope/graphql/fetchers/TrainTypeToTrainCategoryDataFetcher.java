package graphqlscope.graphql.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.TrainCategory;
import graphqlscope.graphql.fetchers.base.OneToOneDataFetcher;
import graphqlscope.graphql.model.TrainCategoryTO;
import graphqlscope.graphql.model.TrainTypeTO;
import graphqlscope.graphql.repositories.TrainCategoryRepository;
import graphqlscope.graphql.to.TrainCategoryTOConverter;

@Component
public class TrainTypeToTrainCategoryDataFetcher extends OneToOneDataFetcher<Long, TrainTypeTO, TrainCategory, TrainCategoryTO> {
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
    public Long createKeyFromParent(TrainTypeTO trainTypeTO) {
        return trainTypeTO.getTrainCategoryId().longValue();
    }

    @Override
    public Long createKeyFromChild(TrainCategory child) {
        return child.id;
    }

    @Override
    public TrainCategoryTO createChildTOToFromChild(TrainCategory child) {
        return trainCategoryTOConverter.convert(child);
    }

    @Override
    public List<TrainCategory> findChildrenByKeys(List<Long> keys) {
        return trainCategoryRepository.findAllById(keys);
    }
}
