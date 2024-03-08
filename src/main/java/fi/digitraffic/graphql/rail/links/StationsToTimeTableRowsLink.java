package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QTimeTableRow;
import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.to.TimeTableRowTOConverter;

@Component
public class StationsToTimeTableRowsLink extends OneToManyLink<String, StationTO, TimeTableRow, TimeTableRowTO> {
    @Autowired
    private TimeTableRowTOConverter timeTableRowTOConverter;

    @Override
    public String getTypeName() {
        return "Station";
    }

    @Override
    public String getFieldName() {
        return "timeTableRows";
    }

    @Override
    public String createKeyFromParent(StationTO stationTO) {
        return stationTO.getShortCode();
    }

    @Override
    public String createKeyFromChild(TimeTableRowTO timeTableRowTO) {
        return timeTableRowTO.getStationShortCode();
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
    public EntityPath getEntityTable() {
        return QTimeTableRow.timeTableRow;
    }

    @Override
    public BooleanExpression createWhere(List<String> keys) {
        return QTimeTableRow.timeTableRow.stationShortCode.in(keys);
    }
}
