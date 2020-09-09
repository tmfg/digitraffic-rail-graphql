package graphqlscope.graphql.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import graphqlscope.graphql.entities.Locomotive;

@Repository
@Transactional
public interface LocomotiveRepository extends JpaRepository<Locomotive, Long> {
    @Query("select e from Locomotive e where e.journeysectionId in ?1")
    List<Locomotive> findAllByJourneySectionIds(List<Long> journeySectionIds);
}
