package fi.digitraffic.graphql.rail.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageStation;

@Repository
@Transactional
public interface PassengerInformationMessageStationRepository extends JpaRepository<PassengerInformationMessageStation, Long> {
}
