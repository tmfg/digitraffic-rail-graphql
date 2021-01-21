package fi.digitraffic.graphql.rail.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TrackRange {
    @Id
    public Long id;

    @AttributeOverrides({
            @AttributeOverride(name = "track", column = @Column(name = "start_track")),
            @AttributeOverride(name = "kilometres", column = @Column(name = "start_kilometres")),
            @AttributeOverride(name = "metres", column = @Column(name = "start_metres")),
    })
    @Embedded
    public TrackLocation startLocation;

    @AttributeOverrides({
            @AttributeOverride(name = "track", column = @Column(name = "end_track")),
            @AttributeOverride(name = "kilometres", column = @Column(name = "end_kilometres")),
            @AttributeOverride(name = "metres", column = @Column(name = "end_metres")),
    })
    @Embedded
    public TrackLocation endLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    public TrackSection trackSection;
}
