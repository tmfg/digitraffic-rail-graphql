package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.links.base.TrainIdWhereClause;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.queries.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.queries.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TimeTableRowTOConverter;
import jakarta.persistence.Tuple;

@Component
public class TrainToTimeTableRowLink extends OneToManyLink<TrainId, TrainTO, TimeTableRow, TimeTableRowTO> {

    private final TimeTableRowTOConverter timeTableRowTOConverter;

    public TrainToTimeTableRowLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                   final JpqlOrderByBuilder jpqlOrderByBuilder,
                                   @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                   final TimeTableRowTOConverter timeTableRowTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.timeTableRowTOConverter = timeTableRowTOConverter;
    }

    @Override
    public boolean cachingEnabled() {
        return false;
    }

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "timeTableRows";
    }

    @Override
    public TrainId createKeyFromParent(final TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber(), trainTO.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(final TimeTableRowTO child) {
        return new TrainId(child.getTrainNumber(), child.getDepartureDate());
    }

    @Override
    public TimeTableRowTO createChildTOFromEntity(final TimeTableRow entity) {
        return timeTableRowTOConverter.convertEntity(entity);
    }

    @Override
    protected String getProjectionExpression() {
        return "e.id.attapId AS attapId, e.id.trainNumber AS trainNumber, e.id.departureDate AS departureDate, " +
                "e.stationShortCode AS stationShortCode, e.stationUICCode AS stationUICCode, e.countryCode AS countryCode, " +
                "e.type AS type, e.trainStopping AS trainStopping, e.commercialStop AS commercialStop, e.commercialTrack AS commercialTrack, " +
                "e.cancelled AS cancelled, e.scheduledTime AS scheduledTime, e.actualTime AS actualTime, e.differenceInMinutes AS differenceInMinutes, " +
                "e.liveEstimateTime AS liveEstimateTime, e.estimateSource AS estimateSource, e.unknownDelay AS unknownDelay, e.stopSector AS stopSector";
    }

    @Override
    protected TimeTableRowTO createChildTOFromProjection(final Tuple row) {
        return timeTableRowTOConverter.convertProjection(row);
    }

    @Override
    public Class<TimeTableRow> getEntityClass() {
        return TimeTableRow.class;
    }


    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<TrainId> keys) {
        return TrainIdWhereClause.build(getEntityAlias(), "departureDate", "trainNumber", keys);
    }

    @Override
    public String getDefaultOrderBy() {
        return getEntityAlias() + ".scheduledTime ASC";
    }
}

