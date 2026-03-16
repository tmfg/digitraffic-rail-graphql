package fi.digitraffic.graphql.rail.links;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.links.TimeTableRowToStationLink;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class TimeTableRowToStationLinkTest extends BaseWebMVCTest {

    @Autowired
    private TimeTableRowToStationLink link;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testCreateKeyFromChild() {
        final StationTO stationTO = new StationTO(
                1, true, "FI", List.of(24.0, 60.0), "Helsinki", "HKI", 1, null, null, null
        );

        final String key = link.createKeyFromChild(stationTO);

        assertEquals("HKI", key);
    }

    @Test
    public void testEntityClassIsStation() {
        assertEquals(Station.class, link.getEntityClass());
    }

    @Test
    public void testDefaultOrderBy() {
        assertEquals(link.getEntityAlias() + ".name ASC", link.getDefaultOrderBy());
    }

    @Test
    public void testDirectQueryExecution() {
        // Create test station
        factoryService.getStationFactory().create("HKI", 1, "FI");

        // Directly execute query to test JPQL query building
        final List<Station> stations = entityManager.createQuery(
                "SELECT e FROM Station e WHERE e.shortCode IN :keys ORDER BY e.name ASC",
                Station.class
        ).setParameter("keys", List.of("HKI")).getResultList();

        assertEquals(1, stations.size());
        assertEquals("HKI", stations.get(0).shortCode);
    }

    @Test
    public void testConvertEntityToTO() {
        // Create test station
        factoryService.getStationFactory().create("HKI", 1, "FI");

        final Station station = entityManager.createQuery(
                "SELECT e FROM Station e WHERE e.shortCode = :code",
                Station.class
        ).setParameter("code", "HKI").getSingleResult();

        final StationTO stationTO = link.createChildTOFromEntity(station);

        assertEquals("HKI", stationTO.getShortCode());
        assertEquals("FI", stationTO.getCountryCode());
    }

    @Test
    public void testLinkTypeName() {
        assertEquals("TimeTableRow", link.getTypeName());
    }

    @Test
    public void testLinkFieldName() {
        assertEquals("station", link.getFieldName());
    }

    @Test
    public void testDataLoaderKey() {
        assertEquals("TimeTableRow.station", link.createDataLoaderKey());
    }
}
