package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QTrainType;
import fi.digitraffic.graphql.rail.entities.TrainType;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.model.TrainTypeTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.TrainTypeTOConverter;

@Component
public class TrainToTrainTypeLink extends OneToOneLink<Long, TrainTO, TrainType, TrainTypeTO> {
    @Autowired
    private TrainTypeTOConverter trainTypeTOConverter;

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
    public Long createKeyFromChild(TrainTypeTO trainTypeTO) {
        return trainTypeTO.getId().longValue();
    }

    @Override
    public TrainTypeTO createChildTOFromTuple(Tuple tuple) {
        return trainTypeTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return TrainType.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.TRAIN_TYPE;
    }

    @Override
    public EntityPath getEntityTable() {
        return QTrainType.trainType;
    }

    @Override
    public BooleanExpression createWhere(List<Long> keys) {
        return QTrainType.trainType.id.in(keys);
    }
}
