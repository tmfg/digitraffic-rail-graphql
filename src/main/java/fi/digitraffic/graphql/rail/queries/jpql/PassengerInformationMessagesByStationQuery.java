package fi.digitraffic.graphql.rail.queries.jpql;

import static com.google.common.base.MoreObjects.firstNonNull;

import java.util.Map;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import graphql.schema.DataFetchingEnvironment;

/**
 * JPQL implementation of PassengerInformationMessagesByStationQuery.
 * Filters messages by station short code and optionally by message type.
 */
@Component
public class PassengerInformationMessagesByStationQuery extends PassengerInformationMessagesQuery {

    @Override
    public String getQueryName() {
        return "passengerInformationMessagesByStation";
    }

    @Override
    public String buildBaseQuery(final String alias, final Map<String, Object> parameters) {
        // Need to join with stations collection for the station filter
        return """
            SELECT DISTINCT %s FROM PassengerInformationMessage %s
            JOIN %s.stations st
            WHERE (%s.id.id, %s.id.version) IN (
                SELECT m2.id.id, MAX(m2.id.version)
                FROM PassengerInformationMessage m2
                GROUP BY m2.id.id
            )""".formatted(alias, alias, alias, alias, alias);
    }

    @Override
    public String buildBaseWhereClause(final String alias, final DataFetchingEnvironment env,
                                        final Map<String, Object> parameters) {
        // Start with the base validity conditions from parent
        final String validityClause = super.buildBaseWhereClause(alias, env, parameters);

        final String station = env.getArgument("stationShortCode");
        parameters.put("stationShortCode", station);

        final StringBuilder where = new StringBuilder(validityClause);
        where.append(" AND st.stationShortCode = :stationShortCode");

        final boolean onlyGeneral = firstNonNull(env.getArgument("onlyGeneral"), false);
        if (onlyGeneral) {
            parameters.put("messageType", PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE);
            where.append(" AND ").append(alias).append(".messageType = :messageType");
        }

        return where.toString();
    }
}

