package fi.digitraffic.graphql.rail.factory;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageId;
import fi.digitraffic.graphql.rail.repositories.PassengerInformationMessageRepository;

@Component
public class PassengerInformationMessageFactory {

    @Autowired
    private PassengerInformationMessageRepository passengerInformationMessageRepository;

    @Transactional
    public PassengerInformationMessage create(final String id, final int version, final ZonedDateTime startValidity, final ZonedDateTime endValidity,
                                              final LocalDate trainDepartureDate,
                                              final long trainNumber) {
        final PassengerInformationMessageId messageId = new PassengerInformationMessageId(id, version);

        final PassengerInformationMessage passengerInformationMessage =
                new PassengerInformationMessage(messageId, ZonedDateTime.now(), startValidity, endValidity,
                        trainDepartureDate, trainNumber, null, null, null);

        return passengerInformationMessageRepository.save(passengerInformationMessage);
    }

    @Transactional
    public PassengerInformationMessage create(final String id, final int version, final ZonedDateTime startValidity,
                                              final ZonedDateTime endValidity) {
        final PassengerInformationMessageId messageId = new PassengerInformationMessageId(id, version);

        final PassengerInformationMessage passengerInformationMessage =
                new PassengerInformationMessage(messageId, ZonedDateTime.now(), startValidity, endValidity,
                        null, null, null, null, null);

        return passengerInformationMessageRepository.save(passengerInformationMessage);
    }
}