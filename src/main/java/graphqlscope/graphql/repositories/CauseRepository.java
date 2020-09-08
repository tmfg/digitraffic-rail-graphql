package graphqlscope.graphql.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import graphqlscope.graphql.entities.Cause;
import graphqlscope.graphql.entities.TimeTableRowId;

@Repository
@Transactional
public interface CauseRepository extends JpaRepository<Cause, Long> {
    @Query("select c from Cause c where c.timeTableRow.id in ?1")
    List<Cause> findAllByTimeTableRowIds(Iterable<TimeTableRowId> timeTableRowIds);
}
