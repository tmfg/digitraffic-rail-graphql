package fi.digitraffic.graphql.rail.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.Routeset;
import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;

@Repository
@Transactional
public interface RoutesetRepository extends JpaRepository<Routeset, Long> {
    @Query("select ttr from Routeset ttr where ttr.trainId in ?1")
    List<Routeset> findAllByTrainIds(Iterable<StringVirtualDepartureDateTrainId> trainIds);

    List<Routeset> findByVersionGreaterThanOrderByVersionAsc(Long version, Pageable pageable);
}
