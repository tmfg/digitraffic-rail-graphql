package fi.digitraffic.graphql.rail.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageId;

@Repository
@Transactional(readOnly = true)
public interface PassengerInformationMessageRepository extends JpaRepository<PassengerInformationMessage, PassengerInformationMessageId> {
}
