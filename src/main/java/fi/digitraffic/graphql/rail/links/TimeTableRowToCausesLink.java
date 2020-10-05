package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.Cause;
import fi.digitraffic.graphql.rail.entities.QCause;
import fi.digitraffic.graphql.rail.entities.TimeTableRowId;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.CauseTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.CauseTOConverter;

@Component
public class TimeTableRowToCausesLink extends OneToManyLink<TimeTableRowId, TimeTableRowTO, Cause, CauseTO> {
    @Autowired
    private CauseTOConverter causeTOConverter;

    @Override
    public String getTypeName() {
        return "TimeTableRow";
    }

    @Override
    public String getFieldName() {
        return "causes";
    }

    @Override
    public TimeTableRowId createKeyFromParent(TimeTableRowTO timeTableRow) {
        return new TimeTableRowId(timeTableRow.getId().longValue(), timeTableRow.getDepartureDate(), timeTableRow.getTrainNumber().longValue());
    }

    @Override
    public TimeTableRowId createKeyFromChild(CauseTO causeTO) {
        return new TimeTableRowId(causeTO.getTimeTableRowId().longValue(), causeTO.getDepartureDate(), causeTO.getTrainNumber().longValue());
    }

    @Override
    public CauseTO createChildTOFromTuple(Tuple tuple) {
        return causeTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return Cause.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.CAUSE;
    }

    @Override
    public EntityPath getEntityTable() {
        return QCause.cause;
    }

    @Override
    public BooleanExpression createWhere(List<TimeTableRowId> keys) {
        return QCause.cause.timeTableRowId.in(keys);
    }
}
