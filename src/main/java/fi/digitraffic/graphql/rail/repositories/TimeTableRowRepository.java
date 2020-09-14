package fi.digitraffic.graphql.rail.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.TimeTableRowId;
import fi.digitraffic.graphql.rail.entities.TrainId;

@Repository
@Transactional
public interface TimeTableRowRepository extends JpaRepository<TimeTableRow, TimeTableRowId> {
    @Query("select ttr from TimeTableRow ttr where ttr.train.id in ?1")
    List<TimeTableRow> findAllByTrainIds(Iterable<TrainId> trainIds);
}
