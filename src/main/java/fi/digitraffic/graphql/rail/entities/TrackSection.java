package fi.digitraffic.graphql.rail.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class TrackSection {
    @Id
    public Long id;
    public String station;
    public String trackSectionCode;

    @OneToMany(mappedBy = "trackSection", fetch = FetchType.LAZY)
    public Set<TrackRange> ranges = new HashSet<>();
}
