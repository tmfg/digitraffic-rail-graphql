package fi.digitraffic.graphql.rail.entities;

import jakarta.persistence.Embeddable;

@Embeddable
public class TrackLocation {
    public String track;
    public Integer kilometres;
    public Integer metres;
}
