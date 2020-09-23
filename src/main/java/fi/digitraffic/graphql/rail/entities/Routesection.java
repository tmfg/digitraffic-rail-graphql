package fi.digitraffic.graphql.rail.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Routesection {
    @Id
    @JsonIgnore
    public Long id;

    public String sectionId;
    public String stationCode;
    public String commercialTrackId;

    public Long routesetId;

    public int sectionOrder;
}
