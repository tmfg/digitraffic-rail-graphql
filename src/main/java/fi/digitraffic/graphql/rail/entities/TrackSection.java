package fi.digitraffic.graphql.rail.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class TrackSection {
    @Id
    public Long id;
    public String station;
    public String trackSectionCode;

    @OneToMany(mappedBy = "trackSection", fetch = FetchType.LAZY)
    public Set<TrackRange> ranges = new HashSet<>();
}
