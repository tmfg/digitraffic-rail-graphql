package fi.digitraffic.graphql.rail.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.Wagon;

@Repository
@Transactional
public interface WagonRepository extends JpaRepository<Wagon, Long> {
    @Query("select e from Wagon e where e.journeysectionId in ?1 order by e.journeysectionId,e.location asc")
    List<Wagon> findAllByJourneySectionIds(List<Long> journeySectionIds);
}
