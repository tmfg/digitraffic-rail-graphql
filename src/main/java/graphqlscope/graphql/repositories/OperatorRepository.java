package graphqlscope.graphql.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import graphqlscope.graphql.entities.Operator;

@Repository
@Transactional
public interface OperatorRepository extends JpaRepository<Operator, Long> {

    List<Operator> findByOperatorShortCodeIn(List<String> operatorShortCodes);
}
