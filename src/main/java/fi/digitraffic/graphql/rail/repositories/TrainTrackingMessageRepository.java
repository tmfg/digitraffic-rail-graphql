package fi.digitraffic.graphql.rail.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.TrainTrackingMessage;

@Repository
@Transactional(readOnly = true)
public interface TrainTrackingMessageRepository extends JpaRepository<TrainTrackingMessage, Long> {
}
