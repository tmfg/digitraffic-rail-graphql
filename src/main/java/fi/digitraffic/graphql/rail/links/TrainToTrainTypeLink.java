package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainType;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.model.TrainTypeTO;
import fi.digitraffic.graphql.rail.repositories.TrainTypeRepository;
import fi.digitraffic.graphql.rail.to.TrainTypeTOConverter;

@Component
public class TrainToTrainTypeLink extends OneToOneLink<Long, TrainTO, TrainType, TrainTypeTO> {
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
