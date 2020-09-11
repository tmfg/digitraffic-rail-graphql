package graphqlscope.graphql.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import graphqlscope.graphql.entities.CategoryCode;

@Repository
@Transactional
public interface CategoryCodeRepository extends JpaRepository<CategoryCode, Long> {
}
