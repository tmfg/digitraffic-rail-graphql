package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.links.base.TrainIdWhereClause;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.queries.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.queries.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;

@Component
public class TrainTrackingMessageToTrainLink extends OneToOneLink<TrainId, TrainTrackingMessageTO, Train, TrainTO> {

    private final TrainTOConverter trainTOConverter;

    public TrainTrackingMessageToTrainLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                           final JpqlOrderByBuilder jpqlOrderByBuilder,
                                           @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                           final TrainTOConverter trainTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.trainTOConverter = trainTOConverter;
    }

    @Override
    public String getTypeName() { return "TrainTrackingMessage"; }

    @Override
    public String getFieldName() { return "train"; }

    @Override
    public TrainId createKeyFromParent(final TrainTrackingMessageTO msg) {
        try {
            return new TrainId(Long.parseLong(msg.getTrainNumber()), msg.getDepartureDate());
        } catch (final NumberFormatException e) {
            return new TrainId(-1111L, msg.getDepartureDate());
        }
    }

    @Override
    public TrainId createKeyFromChild(final TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber(), trainTO.getDepartureDate());
    }

    @Override
    public TrainTO createChildTOFromEntity(final Train entity) {
        return trainTOConverter.convertEntity(entity);
    }

    @Override
    public Class<Train> getEntityClass() { return Train.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<TrainId> keys) {
        return TrainIdWhereClause.build(getEntityAlias(), "id.departureDate", "id.trainNumber", keys);
    }
}

