package fi.digitraffic.graphql.rail.links.jpql;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.jpql.OneToManyLinkJpql;
import fi.digitraffic.graphql.rail.links.base.jpql.TrainIdJpqlWhereClause;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.links.base.jpql.KeyWhereClause;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TimeTableRowTOConverter;

@Component
public class TrainToTimeTableRowLink extends OneToManyLinkJpql<TrainId, TrainTO, TimeTableRow, TimeTableRowTO> {

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
    public Class<TimeTableRow> getEntityClass() {
        return TimeTableRow.class;
    }


    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<TrainId> keys) {
        return TrainIdJpqlWhereClause.build(getEntityAlias(), "departureDate", "trainNumber", keys);
    }

    @Override
    public String getDefaultOrderBy() {
        return getEntityAlias() + ".scheduledTime ASC";
    }
}

