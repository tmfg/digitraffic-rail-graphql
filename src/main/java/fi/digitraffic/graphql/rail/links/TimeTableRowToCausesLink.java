package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Cause;
import fi.digitraffic.graphql.rail.entities.TimeTableRowId;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.CauseTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.query.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.query.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.CauseTOConverter;

@Component
public class TimeTableRowToCausesLink extends OneToManyLink<TimeTableRowId, TimeTableRowTO, Cause, CauseTO> {

    private final CauseTOConverter causeTOConverter;

    public TimeTableRowToCausesLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                    final JpqlOrderByBuilder jpqlOrderByBuilder,
                                    @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                    final CauseTOConverter causeTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.causeTOConverter = causeTOConverter;
    }

    @Override
    public String getTypeName() { return "TimeTableRow"; }

    @Override
    public String getFieldName() { return "causes"; }

    @Override
    public TimeTableRowId createKeyFromParent(final TimeTableRowTO timeTableRowTO) {
        return new TimeTableRowId(timeTableRowTO.getId(), timeTableRowTO.getDepartureDate(), timeTableRowTO.getTrainNumber());
    }

    @Override
    public TimeTableRowId createKeyFromChild(final CauseTO causeTO) {
        return new TimeTableRowId(causeTO.getTimeTableRowId(), causeTO.getDepartureDate(), causeTO.getTrainNumber());
    }

    @Override
    public CauseTO createChildTOFromEntity(final Cause entity) {
        return causeTOConverter.convertEntity(entity);
    }

    @Override
    public Class<Cause> getEntityClass() { return Cause.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<TimeTableRowId> keys) {
        return simpleInClause(getEntityAlias() + ".timeTableRowId IN :keys", keys);
    }
}

