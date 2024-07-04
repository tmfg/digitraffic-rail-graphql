package fi.digitraffic.graphql.rail.factory;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageId;
import fi.digitraffic.graphql.rail.entities.PassengerInformationStation;
import fi.digitraffic.graphql.rail.repositories.PassengerInformationMessageRepository;
import fi.digitraffic.graphql.rail.repositories.PassengerInformationStationRepository;

@Component
public class PassengerInformationMessageFactory {

    @Autowired
    private PassengerInformationMessageRepository passengerInformationMessageRepository;

    @Autowired
    private PassengerInformationStationRepository passengerInformationStationRepository;

    @Transactional
    public PassengerInformationMessage create(final String id, final int version, final ZonedDateTime startValidity, final ZonedDateTime endValidity,
                                              final LocalDate trainDepartureDate,
                                              final long trainNumber) {
        final PassengerInformationMessageId messageId = new PassengerInformationMessageId(id, version);

        final PassengerInformationMessage passengerInformationMessage =
                new PassengerInformationMessage(messageId, ZonedDateTime.now(), startValidity, endValidity,
                        trainDepartureDate, trainNumber, null, null, null,
                        PassengerInformationMessage.MessageType.MONITORED_JOURNEY_SCHEDULED_MESSAGE);

        return passengerInformationMessageRepository.save(passengerInformationMessage);
    }

    @Transactional
    public PassengerInformationMessage create(final String id, final int version, final ZonedDateTime startValidity, final ZonedDateTime endValidity,
                                              final LocalDate trainDepartureDate,
                                              final long trainNumber, final List<String> stations) {
        final PassengerInformationMessageId messageId = new PassengerInformationMessageId(id, version);

        final PassengerInformationMessage passengerInformationMessage =
                new PassengerInformationMessage(messageId, ZonedDateTime.now(), startValidity, endValidity,
                        trainDepartureDate, trainNumber, null, null, null,
                        PassengerInformationMessage.MessageType.MONITORED_JOURNEY_SCHEDULED_MESSAGE);

        final PassengerInformationMessage message = passengerInformationMessageRepository.save(passengerInformationMessage);

        for (final String station : stations) {
            passengerInformationStationRepository.save(new PassengerInformationStation(message, station));
        }
        
        return message;
    }

    @Transactional
    public PassengerInformationMessage create(final String id, final int version, final ZonedDateTime startValidity,
                                              final ZonedDateTime endValidity) {
        final PassengerInformationMessageId messageId = new PassengerInformationMessageId(id, version);

        final PassengerInformationMessage passengerInformationMessage =
                new PassengerInformationMessage(messageId, ZonedDateTime.now(), startValidity, endValidity,
                        null, null, null, null, null, PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE);

        return passengerInformationMessageRepository.save(passengerInformationMessage);
    }

    @Transactional
    public PassengerInformationMessage create(final String id, final int version, final ZonedDateTime startValidity,
                                              final ZonedDateTime endValidity, final List<String> stations) {
        final PassengerInformationMessageId messageId = new PassengerInformationMessageId(id, version);

        final PassengerInformationMessage passengerInformationMessage =
                new PassengerInformationMessage(messageId, ZonedDateTime.now(), startValidity, endValidity,
                        null, null, null, null, null, PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE);
        final PassengerInformationMessage message = passengerInformationMessageRepository.save(passengerInformationMessage);
        for (final String station : stations) {
            passengerInformationStationRepository.save(new PassengerInformationStation(message, station));
        }
        return message;
    }

    @Transactional
    public PassengerInformationMessage create(final String id, final int version, final ZonedDateTime startValidity,
                                              final ZonedDateTime endValidity, final List<String> stations, final
                                              PassengerInformationMessage.MessageType messageType) {
        final PassengerInformationMessageId messageId = new PassengerInformationMessageId(id, version);

        final PassengerInformationMessage passengerInformationMessage =
                new PassengerInformationMessage(messageId, ZonedDateTime.now(), startValidity, endValidity, messageType);
        final PassengerInformationMessage message = passengerInformationMessageRepository.save(passengerInformationMessage);
        for (final String station : stations) {
            passengerInformationStationRepository.save(new PassengerInformationStation(message, station));
        }
        return message;
    }
}