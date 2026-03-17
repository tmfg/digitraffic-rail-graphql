package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.queries.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.queries.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.StationTOConverter;

@Component
public class TrainTrackingMessageToPreviousStationLink extends OneToOneLink<String, TrainTrackingMessageTO, Station, StationTO> {

    private final StationTOConverter stationTOConverter;

    public TrainTrackingMessageToPreviousStationLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                                     final JpqlOrderByBuilder jpqlOrderByBuilder,
                                                     @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                                     final StationTOConverter stationTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.stationTOConverter = stationTOConverter;
    }

    @Override
    public String getTypeName() { return "TrainTrackingMessage"; }

    @Override
    public String getFieldName() { return "previousStation"; }

    @Override
    public String createKeyFromParent(final TrainTrackingMessageTO msg) {
        return msg.getPreviousStationShortCode();
    }

    @Override
    public String createKeyFromChild(final StationTO stationTO) {
        return stationTO.getShortCode();
    }

    @Override
    public StationTO createChildTOFromEntity(final Station entity) {
        return stationTOConverter.convertEntity(entity);
    }

    @Override
    public Class<Station> getEntityClass() { return Station.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<String> keys) {
        return simpleInClause(getEntityAlias() + ".shortCode IN :keys", keys);
    }
}

