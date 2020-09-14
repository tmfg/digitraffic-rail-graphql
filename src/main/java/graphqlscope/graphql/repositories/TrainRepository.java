package graphqlscope.graphql.repositories;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import graphqlscope.graphql.entities.Train;
import graphqlscope.graphql.entities.TrainId;

@Repository
@Transactional
public interface TrainRepository extends JpaRepository<Train, TrainId> {

    @Query("select train from Train train where train.id.departureDate = ?1 order by train.id.trainNumber")
    List<Train> findByDepartureDate(LocalDate departureDate, Pageable pageable);

    @Query("select train from Train train where train.id.departureDate = ?1 and train.id.trainNumber > ?2 order by train.id.trainNumber")
    List<Train> findByDepartureDateWithTrainNumberGreaterThan(LocalDate departureDate, Long trainNumber, Pageable pageable);

    List<Train> findByVersionGreaterThanOrderByVersionAsc(Long version, Pageable pageable);

    @Query("select t.id from LiveTimeTableTrain t where " +
            " t.stationShortCode = ?1 and" +
            " t.trainCategoryId in ?8 and" +
            " t.version > ?5 and" +
            " (" +
            " ((t.trainStopping = true or t.trainStopping = ?4) and t.type = 1 and ((t.actualTime BETWEEN ?2 AND ?3) " +
            "   OR (t.actualTime IS NULL AND t.liveEstimateTime BETWEEN ?2 AND ?3)" +
            "   OR (t.actualTime IS NULL AND t.liveEstimateTime IS NULL AND t.scheduledTime BETWEEN ?2 AND ?3)))" +
            " OR " +
            " ((t.trainStopping = true or t.trainStopping = ?4) and t.type = 0 and ((t.actualTime BETWEEN ?6 AND ?7) " +
            "   OR (t.actualTime IS NULL AND t.liveEstimateTime BETWEEN ?6 AND ?7)" +
            "   OR (t.actualTime IS NULL AND t.liveEstimateTime IS NULL AND t.scheduledTime BETWEEN ?6 AND ?7)) " +
            " ) " +
            ")")
    List<TrainId> findLiveTrains(String station, ZonedDateTime startDeparture, ZonedDateTime endDeparture,
                                 Boolean excludeNonstopping, Long version, ZonedDateTime startArrival, ZonedDateTime endArrival, List<Long> trainCategoryIds);

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
    List<Object[]> findLiveTrainsIds(String station, Integer departedTrains, Integer departingTrains, Integer arrivedTrains,
                                     Integer arrivingTrains, Boolean excludeNonstopping, List<Long> trainCategoryIds);
}
