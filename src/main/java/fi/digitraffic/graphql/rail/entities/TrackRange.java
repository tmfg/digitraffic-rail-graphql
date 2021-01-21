package fi.digitraffic.graphql.rail.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class TrackRange {
    @Id
    public Long id;

    public String startTrack;
    public String endTrack;

    public Integer startKilometres;
    public Integer endKilometres;

    public Integer startMetres;
    public Integer endMetres;

    public Long trackSectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trackSection", nullable = false, insertable = false, updatable = false)
    public TrackSection trackSection;
}
