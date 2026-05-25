package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessage;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.links.base.TrainIdWhereClause;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.queries.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.queries.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TrainTrackingTOConverter;
import jakarta.persistence.Tuple;

@Component
public class TrainToTrainTrackingMessagesLink
        extends OneToManyLink<StringVirtualDepartureDateTrainId, TrainTO, TrainTrackingMessage, TrainTrackingMessageTO> {

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
    protected String getProjectionExpression() {
        return "e.id AS id, e.trainId.trainNumber AS trainNumber, e.trainId.virtualDepartureDate AS virtualDepartureDate, " +
                "e.stationShortCode AS stationShortCode, e.nextStationShortCode AS nextStationShortCode, e.previousStationShortCode AS previousStationShortCode, " +
                "e.version AS version, e.timestamp AS timestamp, e.track_section AS track_section, " +
                "e.nextTrackSectionCode AS nextTrackSectionCode, e.previousTrackSectionCode AS previousTrackSectionCode, e.type AS type";
    }

    @Override
    protected TrainTrackingMessageTO createChildTOFromProjection(final Tuple row) {
        return trainTrackingTOConverter.convertProjection(row);
    }

    @Override
    public Class<TrainTrackingMessage> getEntityClass() {
        return TrainTrackingMessage.class;
    }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<StringVirtualDepartureDateTrainId> keys) {
        return TrainIdWhereClause.buildForVirtualDepartureDate(
                getEntityAlias(), "trainId.virtualDepartureDate", "trainId.trainNumber", keys);
    }

    @Override
    public String getDefaultOrderBy() {
        return getEntityAlias() + ".timestamp ASC";
    }
}

