package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainCategory;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.TrainCategoryTO;
import fi.digitraffic.graphql.rail.model.TrainTypeTO;
import fi.digitraffic.graphql.rail.repositories.TrainCategoryRepository;
import fi.digitraffic.graphql.rail.to.TrainCategoryTOConverter;

@Component
public class TrainTypeToTrainCategoryLink extends OneToOneLink<Long, TrainTypeTO, TrainCategory, TrainCategoryTO> {
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
