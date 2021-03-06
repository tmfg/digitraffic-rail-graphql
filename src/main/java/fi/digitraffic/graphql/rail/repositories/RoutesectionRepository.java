package fi.digitraffic.graphql.rail.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.Routesection;

@Repository
@Transactional
public interface RoutesectionRepository extends JpaRepository<Routesection, Long> {
    List<Routesection> findAllByRoutesetIdInOrderBySectionOrderAsc(List<Long> routesetIds);
}
