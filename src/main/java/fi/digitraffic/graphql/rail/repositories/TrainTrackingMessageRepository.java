package fi.digitraffic.graphql.rail.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessage;

@Repository
@Transactional
public interface TrainTrackingMessageRepository extends JpaRepository<TrainTrackingMessage, Long> {
    @Query("select ttr from TrainTrackingMessage ttr where ttr.trainId in ?1")
    List<TrainTrackingMessage> findAllByTrainIds(Iterable<StringVirtualDepartureDateTrainId> trainIds);
}
