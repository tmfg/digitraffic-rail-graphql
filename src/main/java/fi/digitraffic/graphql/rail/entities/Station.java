package fi.digitraffic.graphql.rail.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

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

}
