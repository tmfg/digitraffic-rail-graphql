package fi.digitraffic.graphql.rail.querydsl;

import com.querydsl.core.types.Expression;
import fi.digitraffic.graphql.rail.entities.QTimeTableRow;

public class JoinFields {
    public static Expression[] TIME_TABLE_ROW = new Expression[]{
            QTimeTableRow.timeTableRow.id.attapId,
            QTimeTableRow.timeTableRow.id.departureDate,
            QTimeTableRow.timeTableRow.id.trainNumber,
            QTimeTableRow.timeTableRow.stationShortCode,
    };
}
