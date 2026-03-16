package fi.digitraffic.graphql.rail.links;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageStation;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageStationTO;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.queries.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.queries.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.PassengerInformationMessageStationTOConverter;
import jakarta.persistence.TypedQuery;

/**
 * Links Station to stationMessages with a JOIN on PassengerInformationMessage
 * to filter by max version and message validity.
 */
@Component
public class StationToPassengerInformationMessageStationLink
        extends OneToManyLink<String, StationTO, PassengerInformationMessageStation, PassengerInformationMessageStationTO> {

    private final PassengerInformationMessageStationTOConverter stationTOConverter;

    public StationToPassengerInformationMessageStationLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                                           final JpqlOrderByBuilder jpqlOrderByBuilder,
                                                           @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                                           final PassengerInformationMessageStationTOConverter stationTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.stationTOConverter = stationTOConverter;
    }

    @Override
    public String getTypeName() {
        return "Station";
    }

    @Override
    public String getFieldName() {
        return "stationMessages";
    }

    @Override
    public String createKeyFromParent(final StationTO parent) {
        return parent.getShortCode();
    }

    @Override
    public String createKeyFromChild(final PassengerInformationMessageStationTO child) {
        if (child == null) {
            return null;
        }
        return child.getStationShortCode();
    }

    @Override
    public PassengerInformationMessageStationTO createChildTOFromEntity(final PassengerInformationMessageStation entity) {
        return stationTOConverter.convertEntity(entity);
    }

    @Override
    public Class<PassengerInformationMessageStation> getEntityClass() {
        return PassengerInformationMessageStation.class;
    }


    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<String> keys) {
        return simpleInClause(getEntityAlias() + ".stationShortCode IN :keys", keys);
    }

    /**
     * Override executeQuery to use a JOIN for max version and validity filtering.
     */
    @Override
    protected List<PassengerInformationMessageStation> executeQuery(
            final List<String> keys,
            final Map<String, Object> whereMap,
            final List<Map<String, Object>> orderByList) {

        final String alias = getEntityAlias();
        final ZonedDateTime now = ZonedDateTime.now();

        // Build JPQL with JOIN to PassengerInformationMessage for validity check
        final StringBuilder jpql = new StringBuilder();
        jpql.append("""
            SELECT DISTINCT %s FROM PassengerInformationMessageStation %s
            LEFT JOIN PassengerInformationMessage msg
            ON msg.id.id = %s.messageId AND msg.id.version = %s.messageVersion
            WHERE (%s.messageId, %s.messageVersion) IN (
                SELECT ms2.messageId, MAX(ms2.messageVersion)
                FROM PassengerInformationMessageStation ms2
                GROUP BY ms2.messageId
            )
            AND msg.deleted IS NULL
            AND msg.startValidity <= :now
            AND msg.endValidity > :now""".formatted(alias, alias, alias, alias, alias, alias));

        final Map<String, Object> params = new HashMap<>();
        params.put("now", now);

        // Add key-based where clause
        jpql.append(" AND %s.stationShortCode IN :keys".formatted(alias));
        params.put("keys", keys);

        // Add user-provided where clause
        if (whereMap != null && !whereMap.isEmpty()) {
            final var whereResult = jpqlWhereBuilder.build(alias, replaceOffsetsWithZonedDateTimes(whereMap));
            if (!whereResult.jpql().isEmpty()) {
                jpql.append(" AND ").append(whereResult.jpql());
                params.putAll(whereResult.params());
            }
        }

        // Build ORDER BY clause
        if (orderByList != null && !orderByList.isEmpty()) {
            final String orderByClause = jpqlOrderByBuilder.build(alias, orderByList);
            if (!orderByClause.isEmpty()) {
                jpql.append(" ORDER BY ").append(orderByClause);
            }
        }

        // Execute query
        final TypedQuery<PassengerInformationMessageStation> query =
                entityManager.createQuery(jpql.toString(), PassengerInformationMessageStation.class);
        params.forEach(query::setParameter);

        return query.getResultList();
    }
}
