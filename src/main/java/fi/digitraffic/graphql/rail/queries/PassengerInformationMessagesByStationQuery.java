package fi.digitraffic.graphql.rail.queries;

import static com.google.common.base.MoreObjects.firstNonNull;

import org.springframework.stereotype.Component;

import com.querydsl.core.types.dsl.BooleanExpression;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
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
        final boolean onlyGeneral = firstNonNull(dataFetchingEnvironment.getArgument("onlyGeneral"), false);
        final BooleanExpression whereExpression =
                QPassengerInformationMessage.passengerInformationMessage.stations.any().stationShortCode.eq(station);

        if (onlyGeneral) {
            return whereExpression.and(QPassengerInformationMessage.passengerInformationMessage.messageType.eq(
                    PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE));
        }

        return whereExpression;
    }
}
