package graphqlscope.graphql.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.TrainType;
import graphqlscope.graphql.fetchers.base.OneToOneDataFetcher;
import graphqlscope.graphql.model.TrainTO;
import graphqlscope.graphql.model.TrainTypeTO;
import graphqlscope.graphql.repositories.TrainTypeRepository;
import graphqlscope.graphql.to.TrainTypeTOConverter;

@Component
public class TrainToTrainTypeDataFetcher extends OneToOneDataFetcher<Long, TrainTO, TrainType, TrainTypeTO> {
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
    public Long createKeyFromParent(TrainTO trainTO) {
        return trainTO.getTrainTypeId().longValue();
    }

    @Override
    public Long createKeyFromChild(TrainType child) {
        return child.id;
    }

    @Override
    public TrainTypeTO createChildTOToFromChild(TrainType child) {
        return trainTypeTOConverter.convert(child);
    }

    @Override
    public List<TrainType> findChildrenByKeys(List<Long> keys) {
        return trainTypeRepository.findAllById(keys);
    }
}
