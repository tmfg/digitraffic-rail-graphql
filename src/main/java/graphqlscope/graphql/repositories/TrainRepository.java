package graphqlscope.graphql.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import graphqlscope.graphql.entities.Train;
import graphqlscope.graphql.entities.TrainId;

@Repository
@Transactional
public interface TrainRepository extends JpaRepository<Train, TrainId> {

    @Query("select train from Train train where train.id.departureDate = ?1 order by train.id.trainNumber")
    List<Train> findByDepartureDate(LocalDate departureDate, Pageable pageable);

    List<Train> findByVersionGreaterThanOrderByVersionAsc(Long version, Pageable pageable);
}
