package fi.digitraffic.graphql.rail.queries.jpql;

import java.time.ZonedDateTime;
import java.util.Map;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import graphql.schema.DataFetchingEnvironment;

/**
 * JPQL implementation of PassengerInformationMessagesQuery.
 * Replaces the QueryDSL version with identical functionality.
 *
 * Base query selects only the latest version of each message,
 * and buildBaseWhereClause filters to active, non-deleted messages.
 */
@Component
public class PassengerInformationMessagesQuery extends BaseQueryJpql<PassengerInformationMessage, PassengerInformationMessageTO> {

    @Override
    public String getQueryName() {
        return "passengerInformationMessages";
    }

    @Override
    public Class<PassengerInformationMessage> getEntityClass() {
        return PassengerInformationMessage.class;
    }

    @Override
    public String getEntityAlias() {
        return "m";
    }

    @Override
    public String buildBaseQuery(final String alias, final Map<String, Object> parameters) {
        return """
            SELECT DISTINCT %s FROM PassengerInformationMessage %s
            WHERE (%s.id.id, %s.id.version) IN (
                SELECT m2.id.id, MAX(m2.id.version)
                FROM PassengerInformationMessage m2
                GROUP BY m2.id.id
            )""".formatted(alias, alias, alias, alias);
    }

    @Override
    public boolean baseQueryContainsWhere() {
        return true;
    }

    @Override
    public String buildBaseWhereClause(final String alias, final DataFetchingEnvironment env,
                                        final Map<String, Object> parameters) {
        final ZonedDateTime now = ZonedDateTime.now();
        parameters.put("now", now);

        return "%s.deleted IS NULL AND %s.startValidity <= :now AND %s.endValidity > :now"
                .formatted(alias, alias, alias);
    }

    @Override
    public String getDefaultOrderBy(final String alias) {
        return alias + ".creationDateTime ASC";
    }

    @Override
    public PassengerInformationMessageTO convertEntityToTO(final PassengerInformationMessage entity) {
        return new PassengerInformationMessageTO(
                entity.id.id,
                entity.id.version,
                entity.creationDateTime,
                entity.startValidity,
                entity.endValidity,
                entity.trainDepartureDate,
                entity.trainNumber != null ? entity.trainNumber.intValue() : null,
                null, // train - populated by link
                null, // messageStations - populated by link
                null, // audio - populated by link
                null  // video - populated by link
        );
    }
}
