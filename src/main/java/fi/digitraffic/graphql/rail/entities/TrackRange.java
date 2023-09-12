package fi.digitraffic.graphql.rail.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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
