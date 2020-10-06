package fi.digitraffic.graphql.rail.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
