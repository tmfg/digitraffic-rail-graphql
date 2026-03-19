package fi.digitraffic.graphql.rail.queries;

import java.time.ZonedDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import graphql.schema.DataFetchingEnvironment;

/**
 * Selects only the latest version of each PassengerInformationMessage,
 * filtered to active, non-deleted messages.
 */
@Component
public class PassengerInformationMessagesQuery extends BaseQuery<PassengerInformationMessage, PassengerInformationMessageTO> {

    public PassengerInformationMessagesQuery(final JpqlWhereBuilder whereBuilder,
                                             final JpqlOrderByBuilder orderByBuilder,
                                             @Value("${digitraffic.max-returned-rows}") final int maxResults) {
        super(whereBuilder, orderByBuilder, maxResults);
    }

    @Override
    public String getQueryName() {
        return "passengerInformationMessages";
    }

    @Override
    public Class<PassengerInformationMessage> getEntityClass() {
        return PassengerInformationMessage.class;
    }


    /** JPQL fragment that restricts to the latest version of each message (no WHERE keyword). */
    public static String latestVersionSubquery(final String alias) {
        return "(%s.id.id, %s.id.version) IN (SELECT m2.id.id, MAX(m2.id.version) FROM PassengerInformationMessage m2 GROUP BY m2.id.id)"
                .formatted(alias, alias);
    }

    /** JPQL fragment for active, non-deleted messages within the validity window (no WHERE keyword). */
    public static String activeMessageCondition(final String alias) {
        return "%s.deleted IS NULL AND %s.startValidity <= :now AND %s.endValidity > :now"
                .formatted(alias, alias, alias);
    }

    @Override
    public String buildBaseQuery(final String alias, final Map<String, Object> parameters) {
        return """
            SELECT DISTINCT %s FROM PassengerInformationMessage %s
            WHERE %s
            """.formatted(alias, alias, latestVersionSubquery(alias));
    }

    @Override
    public boolean baseQueryContainsWhere() {
        return true;
    }

    @Override
    public String buildBaseWhereClause(final String alias, final DataFetchingEnvironment env,
                                        final Map<String, Object> parameters) {
        parameters.put("now", ZonedDateTime.now());
        return activeMessageCondition(alias);
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
