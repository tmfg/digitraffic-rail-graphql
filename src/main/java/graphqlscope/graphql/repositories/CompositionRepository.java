package graphqlscope.graphql.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import graphqlscope.graphql.entities.Composition;
import graphqlscope.graphql.entities.TrainId;

@Repository
@Transactional
public interface CompositionRepository extends JpaRepository<Composition, TrainId> {
    @Query("select e from Composition e where e.id.departureDate = ?1")
    List<Composition> findByDepartureDate(LocalDate departureDate);

    @Query("select e from Composition e where e.id in ?1")
    List<Composition> findAllByTrainIds(List<TrainId> parentIds);

    List<Composition> findByVersionGreaterThanOrderByVersionAsc(long version, Pageable pageable);
}
