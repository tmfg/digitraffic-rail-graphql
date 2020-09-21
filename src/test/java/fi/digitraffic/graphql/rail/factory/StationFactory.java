package fi.digitraffic.graphql.rail.factory;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.entities.StationTypeEnum;

@Component
public class StationFactory {
    public Station create(String shortCode, int uicCode, String country) {
        Station station = new Station();
        station.countryCode = country;
        station.name = shortCode;
        station.shortCode = shortCode;
        station.passengerTraffic = true;
        station.latitude = new BigDecimal(1L);
        station.latitude = new BigDecimal(2L);
        station.type = StationTypeEnum.STATION;

        return station;
    }
}
