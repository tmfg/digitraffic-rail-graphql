package fi.digitraffic.graphql.rail.queries.jpql;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * JPQL implementation of PassengerInformationMessagesByTrainQuery.
 * Filters messages by train number and departure date.
 */
@Component
public class PassengerInformationMessagesByTrainQuery extends PassengerInformationMessagesQuery {

    @Override
    public String getQueryName() {
        return "passengerInformationMessagesByTrain";
    }

    @Override
    public String buildBaseWhereClause(final String alias, final DataFetchingEnvironment env,
                                        final Map<String, Object> parameters) {
        // Start with the base validity + deleted conditions from parent
        final String validityClause = super.buildBaseWhereClause(alias, env, parameters);

        final LocalDate departureDate = env.getArgument("departureDate");
        final Long trainNumber = ((Number) env.getArgument("trainNumber")).longValue();

        parameters.put("trainDepartureDate", departureDate);
        parameters.put("trainNumber", trainNumber);

        return validityClause + " AND %s.trainDepartureDate = :trainDepartureDate AND %s.trainNumber = :trainNumber"
                .formatted(alias, alias);
    }
}

