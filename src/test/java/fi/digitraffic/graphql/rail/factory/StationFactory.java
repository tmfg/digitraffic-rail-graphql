package fi.digitraffic.graphql.rail.factory;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.entities.StationTypeEnum;
import fi.digitraffic.graphql.rail.repositories.StationRepository;

@Component
public class StationFactory {
    @Autowired
    private StationRepository stationRepository;

    public Station create(String shortCode, int uicCode, String country) {
        Station station = new Station();
        station.countryCode = country;
        station.name = shortCode;
        station.shortCode = shortCode;
        station.id = Long.valueOf(station.name.hashCode());
        station.passengerTraffic = true;
        station.longitude = new BigDecimal(1L);
        station.latitude = new BigDecimal(2L);
        station.type = StationTypeEnum.STATION;
        station.uicCode = uicCode;

        return stationRepository.save(station);
    }
}
