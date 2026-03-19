package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.queries.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.queries.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TimeTableRowTOConverter;

@Component
public class StationsToTimeTableRowsLink extends OneToManyLink<String, StationTO, TimeTableRow, TimeTableRowTO> {

    private final TimeTableRowTOConverter timeTableRowTOConverter;

    public StationsToTimeTableRowsLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                       final JpqlOrderByBuilder jpqlOrderByBuilder,
                                       @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                       final TimeTableRowTOConverter timeTableRowTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.timeTableRowTOConverter = timeTableRowTOConverter;
    }

    @Override
    public String getTypeName() { return "Station"; }

    @Override
    public String getFieldName() { return "timeTableRows"; }

    @Override
    public String createKeyFromParent(final StationTO stationTO) {
        return stationTO.getShortCode();
    }

    @Override
    public String createKeyFromChild(final TimeTableRowTO timeTableRowTO) {
        return timeTableRowTO.getStationShortCode();
    }

    @Override
    public TimeTableRowTO createChildTOFromEntity(final TimeTableRow entity) {
        return timeTableRowTOConverter.convertEntity(entity);
    }

    @Override
    public Class<TimeTableRow> getEntityClass() { return TimeTableRow.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<String> keys) {
        return simpleInClause(getEntityAlias() + ".stationShortCode IN :keys", keys);
    }
}

