package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QTrainTrackingMessage;
import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessage;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.TrainTrackingTOConverter;

@Component
public class TrainToTrainTrackingMessagesLink extends OneToManyLink<StringVirtualDepartureDateTrainId, TrainTO, TrainTrackingMessage, TrainTrackingMessageTO> {
    @Autowired
    private TrainTrackingTOConverter trainTrackingTOConverter;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "trainTrackingMessages";
    }

    @Override
    public StringVirtualDepartureDateTrainId createKeyFromParent(TrainTO trainTO) {
        return new StringVirtualDepartureDateTrainId(trainTO.getTrainNumber().toString(), trainTO.getDepartureDate());
    }

    @Override
    public StringVirtualDepartureDateTrainId createKeyFromChild(TrainTrackingMessageTO trainTrackingMessageTO) {
        return new StringVirtualDepartureDateTrainId(trainTrackingMessageTO.getTrainNumber(), trainTrackingMessageTO.getDepartureDate());
    }

    @Override
    public TrainTrackingMessageTO createChildTOFromTuple(Tuple tuple) {
        return trainTrackingTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return TrainTrackingMessage.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.TRAIN_TRACKING_MESSAGE;
    }

    @Override
    public EntityPath getEntityTable() {
        return QTrainTrackingMessage.trainTrackingMessage;
    }

    @Override
    public BooleanExpression createWhere(List<StringVirtualDepartureDateTrainId> keys) {
        return QTrainTrackingMessage.trainTrackingMessage.trainId.in(keys);
    }
}
