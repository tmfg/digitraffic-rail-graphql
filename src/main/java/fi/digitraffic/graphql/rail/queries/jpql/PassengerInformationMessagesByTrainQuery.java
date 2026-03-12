package fi.digitraffic.graphql.rail.queries.jpql;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import graphql.schema.DataFetchingEnvironment;

@Component
public class PassengerInformationMessagesByTrainQuery extends PassengerInformationMessagesQuery {

    public PassengerInformationMessagesByTrainQuery(final JpqlWhereBuilder whereBuilder,
                                                    final JpqlOrderByBuilder orderByBuilder,
                                                    @Value("${digitraffic.max-returned-rows}") final int maxResults) {
        super(whereBuilder, orderByBuilder, maxResults);
    }

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

