package fi.digitraffic.graphql.rail.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.JourneySection;
import fi.digitraffic.graphql.rail.entities.TrainId;

@Repository
@Transactional
public interface JourneySectionRepository extends JpaRepository<JourneySection, Long> {
    @Query("select e from JourneySection e where e.trainId in ?1")
    List<JourneySection> findAllByTrainIds(List<TrainId> trainIds);
}
