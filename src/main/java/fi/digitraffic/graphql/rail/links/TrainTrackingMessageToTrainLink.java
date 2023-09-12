package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QTrain;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;

@Component
public class TrainTrackingMessageToTrainLink extends OneToOneLink<TrainId, TrainTrackingMessageTO, Train, TrainTO> {
    @Autowired
    private TrainTOConverter trainTOConverter;

    @Override
    public String getTypeName() {
        return "TrainTrackingMessage";
    }

    @Override
    public String getFieldName() {
        return "train";
    }

    @Override
    public TrainId createKeyFromParent(final TrainTrackingMessageTO trainTrackingMessageTO) {
        try {
            return new TrainId(Long.parseLong(trainTrackingMessageTO.getTrainNumber()), trainTrackingMessageTO.getDepartureDate());
        } catch (NumberFormatException e) {
            return new TrainId(-1111L, trainTrackingMessageTO.getDepartureDate());
        }
    }

    @Override
    public TrainId createKeyFromChild(final TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber(), trainTO.getDepartureDate());
    }

    @Override
    public TrainTO createChildTOFromTuple(final Tuple tuple) {
        return trainTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return Train.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.TRAIN;
    }

    @Override
    public EntityPath getEntityTable() {
        return QTrain.train;
    }

    @Override
    public BooleanExpression createWhere(final List<TrainId> keys) {
        return QTrain.train.id.in(keys);
    }
}
