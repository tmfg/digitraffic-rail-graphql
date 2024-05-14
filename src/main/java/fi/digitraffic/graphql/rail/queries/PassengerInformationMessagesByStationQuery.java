package fi.digitraffic.graphql.rail.queries;

import org.springframework.stereotype.Component;

import com.querydsl.core.types.dsl.BooleanExpression;

import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import graphql.schema.DataFetchingEnvironment;

@Component
public class PassengerInformationMessagesByStationQuery extends PassengerInformationMessagesQuery {

    @Override
    public String getQueryName() {
        return "passengerInformationMessagesByStation";
    }

    @Override
    public BooleanExpression createWhereFromArguments(final DataFetchingEnvironment dataFetchingEnvironment) {
        final String station = dataFetchingEnvironment.getArgument("stationShortCode");
        return QPassengerInformationMessage.passengerInformationMessage.stations.any().stationShortCode.eq(station);
    }
}
