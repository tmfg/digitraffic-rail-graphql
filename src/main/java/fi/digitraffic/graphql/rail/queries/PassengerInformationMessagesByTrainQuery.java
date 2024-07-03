package fi.digitraffic.graphql.rail.queries;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

import com.querydsl.core.types.dsl.BooleanExpression;

import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import graphql.schema.DataFetchingEnvironment;

@Component
public class PassengerInformationMessagesByTrainQuery extends PassengerInformationMessagesQuery {

    @Override
    public String getQueryName() {
        return "passengerInformationMessagesByTrain";
    }

    @Override
    public BooleanExpression createWhereFromArguments(final DataFetchingEnvironment dataFetchingEnvironment) {
        final LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");
        final Long trainNumber = ((Number) dataFetchingEnvironment.getArgument("trainNumber")).longValue();
        return QPassengerInformationMessage.passengerInformationMessage.trainDepartureDate.eq(departureDate)
                .and(QPassengerInformationMessage.passengerInformationMessage.trainNumber.eq(trainNumber)).and(
                        QPassengerInformationMessage.passengerInformationMessage.endValidity.after(ZonedDateTime.now()).and(
                                QPassengerInformationMessage.passengerInformationMessage.startValidity.before(ZonedDateTime.now())));
    }

}
