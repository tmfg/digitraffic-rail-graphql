package fi.digitraffic.graphql.rail.entities;

import javax.persistence.Embeddable;

@Embeddable
public class TrackLocation {
    public String track;
    public Integer kilometres;
    public Integer metres;
}
