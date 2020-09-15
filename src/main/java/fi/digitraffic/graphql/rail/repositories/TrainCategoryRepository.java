package fi.digitraffic.graphql.rail.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.TrainCategory;

@Repository
@Transactional
public interface TrainCategoryRepository extends JpaRepository<TrainCategory, Long> {
    @Query("select t.id from TrainCategory  t where t.name in ?1")
    List<Long> findAllByNameIn(List<String> names);
}
