package graphqlscope.graphql.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import graphqlscope.graphql.entities.TrainType;

@Repository
@Transactional
public interface TrainTypeRepository extends JpaRepository<TrainType, Long> {
}
