package fi.digitraffic.graphql.rail.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Station {
    @JsonProperty("stationName")
    public String name;
    @JsonProperty("stationShortCode")
    public String shortCode;
    @JsonProperty("stationUICCode")
    public int uicCode;
    public String countryCode;
    public Double longitude;
    public Double latitude;

    @Id
    public Long id;

    public Boolean passengerTraffic;
    public StationTypeEnum type;
}
