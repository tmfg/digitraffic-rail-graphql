package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QTimeTableRow;
import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.TimeTableRowId;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.TimeTableRowTOConverter;

@Component
public class JourneySectionToStartTimeTableRowLink extends OneToOneLink<TimeTableRowId, JourneySectionTO, TimeTableRow, TimeTableRowTO> {
    @Autowired
    private TimeTableRowTOConverter timeTableRowTOConverter;

    @Override
    public String getTypeName() {
        return "JourneySection";
    }

    @Override
    public String getFieldName() {
        return "startTimeTableRow";
    }

    @Override
    public TimeTableRowId createKeyFromParent(JourneySectionTO journeySectionTO) {
        return new TimeTableRowId(journeySectionTO.getBeginTimeTableRowId(), journeySectionTO.getDepartureDate(), journeySectionTO.getTrainNumber().longValue());
    }

    @Override
    public TimeTableRowId createKeyFromChild(TimeTableRowTO timeTableRowTO) {
        return new TimeTableRowId(timeTableRowTO.getId(), timeTableRowTO.getDepartureDate(), timeTableRowTO.getTrainNumber().longValue());
    }

    @Override
    public TimeTableRowTO createChildTOFromTuple(Tuple tuple) {
        return timeTableRowTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return TimeTableRow.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.TIME_TABLE_ROW;
    }

    @Override
    public EntityPath getEntityTable() {
        return QTimeTableRow.timeTableRow;
    }

    @Override
    public BooleanExpression createWhere(List<TimeTableRowId> keys) {
        return QTimeTableRow.timeTableRow.id.in(keys);
    }

}
