package fi.digitraffic.graphql.rail.links.jpql;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageStation;
import fi.digitraffic.graphql.rail.links.base.jpql.OneToManyLinkJpql;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageStationTO;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.to.PassengerInformationMessageStationTOConverter;
import jakarta.persistence.TypedQuery;

/**
 * JPQL implementation: Station → stationMessages (OneToMany).
 *
 * This is a complex link that requires a JOIN with PassengerInformationMessage
 * to filter by max version and message validity conditions.
 * Mirrors the QueryDSL StationToPassengerInformationMessageStationLink.
 */
@Component
public class StationToPassengerInformationMessageStationLink
        extends OneToManyLinkJpql<String, StationTO, PassengerInformationMessageStation, PassengerInformationMessageStationTO> {

    @Autowired
    private PassengerInformationMessageStationTOConverter stationTOConverter;

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
    public String createWhereClause(final List<String> keys) {
        return "e.stationShortCode IN :keys";
    }

    /**
     * Override to use a custom query with JOIN for max version + validity filtering.
     * This replicates getPassengerInformationMessageStationBaseQuery from the QueryDSL version.
     */
    @Override
    protected List<PassengerInformationMessageStation> executeQuery(
            final List<String> keys,
            final Map<String, Object> whereMap,
            final List<Map<String, Object>> orderByList) {

        final String alias = "e";
        final ZonedDateTime now = ZonedDateTime.now();

        // Build JPQL with JOIN to PassengerInformationMessage for validity check
        final StringBuilder jpql = new StringBuilder();
        jpql.append("""
            SELECT DISTINCT e FROM PassengerInformationMessageStation e
            LEFT JOIN PassengerInformationMessage msg
            ON msg.id.id = e.messageId AND msg.id.version = e.messageVersion
            WHERE (e.messageId, e.messageVersion) IN (
                SELECT ms2.messageId, MAX(ms2.messageVersion)
                FROM PassengerInformationMessageStation ms2
                GROUP BY ms2.messageId
            )
            AND msg.deleted IS NULL
            AND msg.startValidity <= :now
            AND msg.endValidity > :now""");

        final Map<String, Object> params = new HashMap<>();
        params.put("now", now);

        // Add key-based where clause
        jpql.append(" AND e.stationShortCode IN :keys");
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

