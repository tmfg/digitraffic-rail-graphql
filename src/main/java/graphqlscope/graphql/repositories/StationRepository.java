package graphqlscope.graphql.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import graphqlscope.graphql.entities.Station;

@Repository
@Transactional
public interface StationRepository extends JpaRepository<Station, Long> {
    List<Station> findByShortCodeIn(List<String> stationShortCode);
}
