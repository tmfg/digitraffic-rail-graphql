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
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.querydsl.JoinFields;
import fi.digitraffic.graphql.rail.to.TimeTableRowTOConverter;

@Component
public class TrainToTimeTableRowLink extends OneToManyLink<TrainId, TrainTO, TimeTableRow, TimeTableRowTO> {
    @Autowired
    private TimeTableRowTOConverter timeTableRowTOConverter;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "timeTableRows";
    }

    @Override
    public TrainId createKeyFromParent(TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber().longValue(), trainTO.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(TimeTableRowTO child) {
        return new TrainId(child.getTrainNumber(), child.getDepartureDate());
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
        return JoinFields.TIME_TABLE_ROW;
    }

    @Override
    public EntityPath getEntityTable() {
        return QTimeTableRow.timeTableRow;
    }

    @Override
    public BooleanExpression createWhere(List<TrainId> keys) {
        return QTimeTableRow.timeTableRow.train.id.in(keys);
    }
}
