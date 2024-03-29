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
import fi.digitraffic.graphql.rail.repositories.TrainIdOptimizer;
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
    public StringVirtualDepartureDateTrainId createKeyFromParent(final TrainTO trainTO) {
        return new StringVirtualDepartureDateTrainId(String.valueOf(trainTO.getTrainNumber()), trainTO.getDepartureDate());
    }

    @Override
    public StringVirtualDepartureDateTrainId createKeyFromChild(final TrainTrackingMessageTO trainTrackingMessageTO) {
        return new StringVirtualDepartureDateTrainId(String.valueOf(trainTrackingMessageTO.getTrainNumber()), trainTrackingMessageTO.getDepartureDate());
    }

    @Override
    public TrainTrackingMessageTO createChildTOFromTuple(final Tuple tuple) {
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
    public BooleanExpression createWhere(final List<StringVirtualDepartureDateTrainId> keys) {
        return TrainIdOptimizer.optimize(QTrainTrackingMessage.trainTrackingMessage.trainId, keys);
    }
}
