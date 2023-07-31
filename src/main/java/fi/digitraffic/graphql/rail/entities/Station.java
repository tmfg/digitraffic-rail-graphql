package fi.digitraffic.graphql.rail.entities;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Station {
    @JsonProperty("stationName")
    public String name;
    @JsonProperty("stationShortCode")
    public String shortCode;
    @JsonProperty("stationUICCode")
    public int uicCode;
    public String countryCode;
    public BigDecimal longitude;
    public BigDecimal latitude;

    @Id
    public Long id;

    public Boolean passengerTraffic;
    public StationTypeEnum type;
}
