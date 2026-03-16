package fi.digitraffic.graphql.rail.links.jpql;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.TimeTableRowId;
import fi.digitraffic.graphql.rail.links.base.jpql.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.jpql.OneToOneLinkJpql;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TimeTableRowTOConverter;

@Component
public class JourneySectionToStartTimeTableRowLink extends OneToOneLinkJpql<TimeTableRowId, JourneySectionTO, TimeTableRow, TimeTableRowTO> {

    private final TimeTableRowTOConverter timeTableRowTOConverter;

    public JourneySectionToStartTimeTableRowLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                                 final JpqlOrderByBuilder jpqlOrderByBuilder,
                                                 @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                                 final TimeTableRowTOConverter timeTableRowTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.timeTableRowTOConverter = timeTableRowTOConverter;
    }

    @Override
    public String getTypeName() { return "JourneySection"; }

    @Override
    public String getFieldName() { return "startTimeTableRow"; }

    @Override
    public TimeTableRowId createKeyFromParent(final JourneySectionTO journeySectionTO) {
        Integer beginId = journeySectionTO.getBeginTimeTableRowId();
        if (beginId == null) {
            beginId = -1;
        }
        return new TimeTableRowId(beginId, journeySectionTO.getDepartureDate(), journeySectionTO.getTrainNumber());
    }

    @Override
    public TimeTableRowId createKeyFromChild(final TimeTableRowTO timeTableRowTO) {
        return new TimeTableRowId(timeTableRowTO.getId(), timeTableRowTO.getDepartureDate(), timeTableRowTO.getTrainNumber());
    }

    @Override
    public TimeTableRowTO createChildTOFromEntity(final TimeTableRow entity) {
        return timeTableRowTOConverter.convertEntity(entity);
    }

    @Override
    public Class<TimeTableRow> getEntityClass() { return TimeTableRow.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<TimeTableRowId> keys) {
        return simpleInClause(getEntityAlias() + ".id IN :keys", keys);
    }
}

