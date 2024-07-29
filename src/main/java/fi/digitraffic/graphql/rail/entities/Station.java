package fi.digitraffic.graphql.rail.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Station {
    public String name;
    public String shortCode;
    public int uicCode;
    public String countryCode;
    public Double longitude;
    public Double latitude;

    @Id
    public Long id;

    public Boolean passengerTraffic;
    public StationTypeEnum type;

    @OneToMany(mappedBy = "station",
               fetch = FetchType.LAZY)
    private List<PassengerInformationMessageStation> passengerInformationMessageStations;

}
