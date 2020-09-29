package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Operator;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.OperatorTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.repositories.OperatorRepository;
import fi.digitraffic.graphql.rail.to.OperatorTOConverter;

@Component
public class TrainToOperatorLink extends OneToOneLink<String, TrainTO, Operator, OperatorTO> {
    @Autowired
    private OperatorRepository operatorRepository;

    @Autowired
    private OperatorTOConverter operatorTOConverter;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "operator";
    }

    @Override
    public String createKeyFromParent(TrainTO trainTO) {
        return trainTO.getOperatorShortCode();
    }

    @Override
    public String createKeyFromChild(Operator child) {
        return child.shortCode;
    }

    @Override
    public OperatorTO createChildTOToFromChild(Operator child) {
        return operatorTOConverter.convert(child);
    }

    @Override
    public List<Operator> findChildrenByKeys(List<String> keys) {
        return operatorRepository.findByShortCodeIn(keys);
    }
}
