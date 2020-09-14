package fi.digitraffic.graphql.rail.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.entities.TrainLocation;

@Repository
@Transactional
public interface TrainLocationRepository extends JpaRepository<TrainLocation, TrainId> {
    @Query("select tl from TrainLocation tl where tl.train.id in ?1")
    List<TrainLocation> findAllByTrainIds(Iterable<TrainId> trainIds);
}
