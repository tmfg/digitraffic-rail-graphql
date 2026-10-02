package fi.digitraffic.graphql.rail.webmvc;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.TrainFactory;
import fi.digitraffic.graphql.rail.factory.TrainLocationFactory;


public class TrainLocationQueriesTest extends BaseWebMVCTest {
    @Autowired
    private TrainLocationFactory trainLocationFactory;

    @Autowired
    private TrainFactory trainFactory;

    @Test
    public void speedOver100ShouldWork() throws Exception {
        final Train train66 = trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train67 = trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2000, 1, 1))).getFirst();

        trainLocationFactory.create(1, 2, 99, train66);
        trainLocationFactory.create(1, 2, 100, train66);
        trainLocationFactory.create(1, 2, 101, train66);
        trainLocationFactory.create(1, 2, 102, train67);

        final ResultActions result = this.query("{   latestTrainLocations(where: {speed: {greaterThan: 100}}) {    speed    train {      trainNumber      departureDate    }  }}");
        result.andExpect(jsonPath("$.data.latestTrainLocations.length()").value(2));
    }

    /*
    @Test
    public void coordinateFilteringShouldWork() throws Exception {
        final Train train66 = trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train67 = trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train68 = trainFactory.createBaseTrain(new TrainId(68L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train69 = trainFactory.createBaseTrain(new TrainId(69L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train70 = trainFactory.createBaseTrain(new TrainId(70L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train71 = trainFactory.createBaseTrain(new TrainId(71L, LocalDate.of(2000, 1, 1))).getFirst();

        trainLocationFactory.create(1, 1, 100, train66);
        trainLocationFactory.create(2, 3, 100, train67);
        trainLocationFactory.create(4, 4, 100, train68);
        trainLocationFactory.create(3, 2, 100, train69);
        trainLocationFactory.create(4, 3, 100, train70);
        trainLocationFactory.create(5, 1, 100, train71);

        final ResultActions result = this.query("{   latestTrainLocations(where: {location: {inside: [3,2,5,4]}}) {    location    train {      trainNumber      departureDate    }  }}");
        result.andExpect(jsonPath("$.data.latestTrainLocations.length()").value(3));
    }*/
    @Test
    public void nestedSortingShouldWork() throws Exception {
        final Train train66 = trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train67 = trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train68 = trainFactory.createBaseTrain(new TrainId(68L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train69 = trainFactory.createBaseTrain(new TrainId(69L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train70 = trainFactory.createBaseTrain(new TrainId(70L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train71 = trainFactory.createBaseTrain(new TrainId(71L, LocalDate.of(2000, 1, 1))).getFirst();

        trainLocationFactory.create(1, 1, 100, train66);
        trainLocationFactory.create(2, 3, 100, train70);
        trainLocationFactory.create(4, 4, 100, train68);
        trainLocationFactory.create(3, 2, 100, train69);
        trainLocationFactory.create(4, 3, 100, train67);
        trainLocationFactory.create(5, 1, 100, train71);

        final ResultActions result = this.query("""
        {
            latestTrainLocations(orderBy: {train:{trainNumber:DESCENDING}}) {
                speed
                train {
                    trainNumber
                    departureDate
                }
            }
        }
        """);
        result.andExpect(jsonPath("$.data.latestTrainLocations.length()").value(6));
        result.andExpect(jsonPath("$.data.latestTrainLocations[0].train.trainNumber").value(71));
        result.andExpect(jsonPath("$.data.latestTrainLocations[1].train.trainNumber").value(70));
        result.andExpect(jsonPath("$.data.latestTrainLocations[2].train.trainNumber").value(69));
        result.andExpect(jsonPath("$.data.latestTrainLocations[3].train.trainNumber").value(68));
        result.andExpect(jsonPath("$.data.latestTrainLocations[4].train.trainNumber").value(67));
        result.andExpect(jsonPath("$.data.latestTrainLocations[5].train.trainNumber").value(66));
    }

    @Test
    public void timestampShouldBeValidIso8601() throws Exception {
        final Train train = trainFactory.createBaseTrain(new TrainId(80L, LocalDate.of(2000, 1, 1))).getFirst();
        trainLocationFactory.create(25.0, 60.0, 100, 5, train);

        final ResultActions result = query("{ latestTrainLocations { timestamp } }");
        // NOTE: GraphQL uses ExtendedScalars.DateTime (ISO_OFFSET_DATE_TIME: variable fractional seconds, numeric
        // offset or Z). Unlike REST it does NOT guarantee the fixed yyyy-MM-dd'T'HH:mm:ss.SSS'Z' format — a contract
        // difference to preserve/decide during the PALA migration.
        result.andExpect(jsonPath("$.data.latestTrainLocations[0].timestamp",
                matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?(Z|[+-]\\d{2}:\\d{2})")));
    }

    @Test
    public void locationShouldBeFloatArray() throws Exception {
        final Train train = trainFactory.createBaseTrain(new TrainId(81L, LocalDate.of(2000, 1, 1))).getFirst();
        trainLocationFactory.create(25.0, 60.0, 100, 5, train);

        final ResultActions result = query("{ latestTrainLocations { location } }");
        result.andExpect(jsonPath("$.data.latestTrainLocations[0].location[0]").value(25.0));
        result.andExpect(jsonPath("$.data.latestTrainLocations[0].location[1]").value(60.0));
        result.andExpect(jsonPath("$.data.latestTrainLocations[0].location.length()").value(2));
    }

    @Test
    public void accuracyShouldAppearWhenNonNull() throws Exception {
        final Train train = trainFactory.createBaseTrain(new TrainId(82L, LocalDate.of(2000, 1, 1))).getFirst();
        trainLocationFactory.create(25.0, 60.0, 100, 5, train);

        final ResultActions result = query("{ latestTrainLocations { accuracy } }");
        result.andExpect(jsonPath("$.data.latestTrainLocations[0].accuracy").value(5));
    }

    @Test
    public void accuracyShouldBeNullWhenNull() throws Exception {
        final Train train = trainFactory.createBaseTrain(new TrainId(83L, LocalDate.of(2000, 1, 1))).getFirst();
        trainLocationFactory.create(25.0, 60.0, 100, null, train);

        final ResultActions result = query("{ latestTrainLocations { accuracy } }");
        // GraphQL returns an explicit null (unlike REST, which omits the field entirely)
        result.andExpect(jsonPath("$.data.latestTrainLocations[0].accuracy").value(nullValue()));
    }

    @Test
    public void speedShouldBePresentAndNonNull() throws Exception {
        final Train train = trainFactory.createBaseTrain(new TrainId(84L, LocalDate.of(2000, 1, 1))).getFirst();
        trainLocationFactory.create(25.0, 60.0, 100, 5, train);

        // Pins the current speed : Int! (non-null) contract. PALA can return nopeus: null, which would violate this.
        final ResultActions result = query("{ latestTrainLocations { speed } }");
        result.andExpect(jsonPath("$.data.latestTrainLocations[0].speed").value(100));
    }

    @Test
    public void isGpsLocationTrueShouldAppearInResponse() throws Exception {
        final Train train = trainFactory.createBaseTrain(new TrainId(85L, LocalDate.of(2000, 1, 1))).getFirst();
        trainLocationFactory.create(25.0, 60.0, 100, 5, true, train);

        final ResultActions result = query("{ latestTrainLocations { isGpsLocation } }");
        result.andExpect(jsonPath("$.data.latestTrainLocations[0].isGpsLocation").value(true));
    }

    @Test
    public void isGpsLocationFalseShouldAppearInResponse() throws Exception {
        final Train train = trainFactory.createBaseTrain(new TrainId(86L, LocalDate.of(2000, 1, 1))).getFirst();
        trainLocationFactory.create(25.0, 60.0, 100, null, false, train);

        final ResultActions result = query("{ latestTrainLocations { isGpsLocation } }");
        result.andExpect(jsonPath("$.data.latestTrainLocations[0].isGpsLocation").value(false));
    }

    @Test
    public void isGpsLocationShouldBeNonNull() throws Exception {
        final Train train = trainFactory.createBaseTrain(new TrainId(87L, LocalDate.of(2000, 1, 1))).getFirst();
        trainLocationFactory.create(25.0, 60.0, 100, 5, train);

        // isGpsLocation : Boolean! (non-null) — every train location must report a value.
        final ResultActions result = query("{ latestTrainLocations { isGpsLocation } }");
        result.andExpect(jsonPath("$.data.latestTrainLocations[0].isGpsLocation").isBoolean());
    }
}
