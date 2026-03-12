package fi.digitraffic.graphql.rail.links.jpql;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessage;
import fi.digitraffic.graphql.rail.links.base.jpql.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.jpql.OneToManyLinkJpql;
import fi.digitraffic.graphql.rail.links.base.jpql.TrainIdJpqlWhereClause;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TrainTrackingTOConverter;

@Component
public class TrainToTrainTrackingMessagesLink
        extends OneToManyLinkJpql<StringVirtualDepartureDateTrainId, TrainTO, TrainTrackingMessage, TrainTrackingMessageTO> {

    private final TrainTrackingTOConverter trainTrackingTOConverter;

    public TrainToTrainTrackingMessagesLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                            final JpqlOrderByBuilder jpqlOrderByBuilder,
                                            @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                            final TrainTrackingTOConverter trainTrackingTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.trainTrackingTOConverter = trainTrackingTOConverter;
    }

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
    public StringVirtualDepartureDateTrainId createKeyFromChild(final TrainTrackingMessageTO child) {
        return new StringVirtualDepartureDateTrainId(child.getTrainNumber(), child.getDepartureDate());
    }

    @Override
    public TrainTrackingMessageTO createChildTOFromEntity(final TrainTrackingMessage entity) {
        return trainTrackingTOConverter.convertEntity(entity);
    }

    @Override
    public Class<TrainTrackingMessage> getEntityClass() {
        return TrainTrackingMessage.class;
    }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<StringVirtualDepartureDateTrainId> keys) {
        return TrainIdJpqlWhereClause.buildForVirtualDepartureDate(
                getEntityAlias(), "trainId.virtualDepartureDate", "trainId.trainNumber", keys);
    }

    @Override
    public String getDefaultOrderBy() {
        return getEntityAlias() + ".timestamp ASC";
    }
}

