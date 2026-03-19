package fi.digitraffic.graphql.rail.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.entities.StationTypeEnum;
import fi.digitraffic.graphql.rail.repositories.StationRepository;

@Component
public class StationFactory {
    @Autowired
    private StationRepository stationRepository;

    public Station create(final String shortCode, final int uicCode, final String country) {
        return createWithPassengerTraffic(shortCode, uicCode, country, true);
    }

    public Station createWithPassengerTraffic(final String shortCode, final int uicCode, final String country, final boolean passengerTraffic) {
        final Station station = new Station();
        station.countryCode = country;
        station.name = shortCode;
        station.shortCode = shortCode;
        station.id = (long) station.name.hashCode();
        station.passengerTraffic = passengerTraffic;
        station.longitude = 1D;
        station.latitude = 2D;
        station.type = StationTypeEnum.STATION;
        station.uicCode = uicCode;

        return stationRepository.save(station);
    }
}
