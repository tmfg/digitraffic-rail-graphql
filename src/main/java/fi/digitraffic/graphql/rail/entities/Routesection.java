package fi.digitraffic.graphql.rail.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stationCode", referencedColumnName = "shortCode", updatable = false, insertable = false)
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routesetId", referencedColumnName = "id", updatable = false, insertable = false)
    private Routeset routeset;
}
