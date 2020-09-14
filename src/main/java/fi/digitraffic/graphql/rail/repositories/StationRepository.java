package fi.digitraffic.graphql.rail.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.Station;

@Repository
@Transactional
public interface StationRepository extends JpaRepository<Station, Long> {
    List<Station> findByShortCodeIn(List<String> stationShortCode);
}
