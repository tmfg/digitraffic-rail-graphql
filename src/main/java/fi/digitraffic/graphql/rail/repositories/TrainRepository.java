package fi.digitraffic.graphql.rail.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;

@Repository
@Transactional
public interface TrainRepository extends JpaRepository<Train, TrainId> {

    @Query("select train from Train train where train.id.departureDate = ?1 order by train.id.trainNumber")
    List<Train> findByDepartureDate(final LocalDate departureDate, final Pageable pageable);

    @Query("select train from Train train where train.id.departureDate = ?1 and train.id.trainNumber > ?2 order by train.id.trainNumber")
    List<Train> findByDepartureDateWithTrainNumberGreaterThan(final LocalDate departureDate, final Long trainNumber, final Pageable pageable);

    List<Train> findByVersionGreaterThanOrderByVersionAsc(final Long version, final Pageable pageable);

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
}
