package fi.digitraffic.graphql.rail.repositories;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;

@Repository
@Transactional(readOnly = true)
public interface TrainRepository extends JpaRepository<Train, TrainId> {
    @Query(value = "select * from ((SELECT " +
        "    '1', t.departure_date, t.train_number, t.version" +
        " FROM" +
        "    live_time_table_train t" +
        " WHERE" +
        "   (t.train_stopping = true or t.train_stopping = ?6)" +
        "   AND (?1 is null OR t.station_short_code = ?1)" +
        "   AND t.type = '1'" +
        "   AND (t.train_category_id in ?7)" +
        "   AND t.actual_time IS NOT NULL" +
        "   AND (t.deleted IS NULL OR t.deleted = 0)" +
        " ORDER BY t.actual_time DESC" +
        " LIMIT ?2) UNION ALL (SELECT " +
        "    '2', t.departure_date, t.train_number, t.version" +
        " FROM" +
        "    live_time_table_train t" +
        " WHERE" +
        "   (t.train_stopping = true or t.train_stopping = ?6)" +
        "   AND (?1 is null OR t.station_short_code = ?1)" +
        "   AND t.type = '1'" +
        "   AND (t.train_category_id in ?7)" +
        "   AND t.actual_time IS NULL" +
        "   AND (t.deleted IS NULL OR t.deleted = 0)" +
        " ORDER BY t.predict_time ASC" +
        " LIMIT ?3) UNION ALL (SELECT " +
        "    '3', t.departure_date, t.train_number, t.version" +
        " FROM" +
        "    live_time_table_train t" +
        " WHERE" +
        "   (t.train_stopping = true or t.train_stopping = ?6)" +
        "   AND (?1 is null OR t.station_short_code = ?1)" +
        "   AND t.type = '0'" +
        "   AND (t.train_category_id in ?7)" +
        "   AND t.actual_time IS NOT NULL" +
        "   AND (t.deleted IS NULL OR t.deleted = 0)" +
        " ORDER BY t.actual_time DESC" +
        " LIMIT ?4) UNION ALL (SELECT " +
        "    '4', t.departure_date, t.train_number, t.version" +
        " FROM" +
        "    live_time_table_train t" +
        " WHERE" +
        "   (t.train_stopping = true or t.train_stopping = ?6)" +
        "   AND (?1 is null OR t.station_short_code = ?1)" +
        "   AND t.type = '0'" +
        "   AND (t.train_category_id in ?7)" +
        "   AND t.actual_time IS NULL" +
        "   AND (t.deleted IS NULL OR t.deleted = 0)" +
        " ORDER BY t.predict_time ASC" +
        " LIMIT ?5)) unionedTable", nativeQuery = true)
    List<Object[]> findLiveTrainsIds(final String station, final Integer departedTrains, final Integer departingTrains, final Integer arrivedTrains,
                                     final Integer arrivingTrains, final Boolean excludeNonstopping, final List<Long> trainCategoryIds);

    /**
     * Find trains by departure and arrival stations with scheduled time filtering.
     * This is a simplified version for GraphQL that returns TrainIds instead of full Train objects.
     * The actual SQL mimics the REST API's findByStationsAndScheduledDate but adapted for GraphQL's data loading pattern.
     *
     * @param departureStation departure station short code
     * @param arrivalStation arrival station short code
     * @param scheduledStart start of scheduled time window
     * @param scheduledEnd end of scheduled time window
     * @param departureDateStart start of departure date range
     * @param departureDateEnd end of departure date range
     * @param includeNonStopping whether to include non-stopping trains
     * @param limit maximum number of results
     * @return list of TrainIds matching the criteria
     */
    @Query(value = """
        SELECT t.train_number, t.departure_date
        FROM train t
        WHERE (t.deleted IS NULL OR t.deleted = 0)
          AND t.departure_date BETWEEN ?5 AND ?6
          AND EXISTS (
            SELECT 1
            FROM time_table_row ttrDeparture
            WHERE ttrDeparture.train_number = t.train_number
              AND ttrDeparture.departure_date = t.departure_date
              AND ttrDeparture.station_short_code = ?1
              AND ttrDeparture.type = 1
              AND (ttrDeparture.train_stopping = true OR ?7 = true)
              AND ttrDeparture.scheduled_time BETWEEN ?3 AND ?4
              AND EXISTS (
                SELECT 1
                FROM time_table_row ttrArrival
                WHERE ttrArrival.train_number = t.train_number
                  AND ttrArrival.departure_date = t.departure_date
                  AND ttrArrival.station_short_code = ?2
                  AND ttrArrival.type = 0
                  AND (ttrArrival.train_stopping = true OR ?7 = true)
                  AND ttrArrival.scheduled_time >= ttrDeparture.scheduled_time
              )
          )
        ORDER BY t.departure_date, t.train_number
        LIMIT ?8
        """, nativeQuery = true)
    List<Object[]> findTrainsByRouteRaw(
        String departureStation,
        String arrivalStation,
        ZonedDateTime scheduledStart,
        ZonedDateTime scheduledEnd,
        LocalDate departureDateStart,
        LocalDate departureDateEnd,
        Boolean includeNonStopping,
        Integer limit
    );

    /**
     * Default method that converts raw results to TrainId objects
     */
    default List<TrainId> findByStationsAndScheduledDate(
        final String departureStation,
        final String arrivalStation,
        final ZonedDateTime scheduledStart,
        final ZonedDateTime scheduledEnd,
        final LocalDate departureDateStart,
        final LocalDate departureDateEnd,
        final Boolean includeNonStopping,
        final Integer limit
    ) {
        return findTrainsByRouteRaw(departureStation, arrivalStation, scheduledStart, scheduledEnd,
                                    departureDateStart, departureDateEnd, includeNonStopping, limit)
            .stream()
            .map(row -> {
                Long trainNumber = ((Number) row[0]).longValue();
                LocalDate departureDate = ((java.sql.Date) row[1]).toLocalDate();
                return new TrainId(trainNumber, departureDate);
            })
            .collect(Collectors.toList());
    }
}
