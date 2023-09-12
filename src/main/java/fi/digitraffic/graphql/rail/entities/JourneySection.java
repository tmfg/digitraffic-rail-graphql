package fi.digitraffic.graphql.rail.entities;

import java.util.Set;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class JourneySection {
    @Id
    public Long id;
    @Embedded
    public TrainId trainId;

    public Long attapId;
    public Long saapAttapId;

    public int totalLength;
    public int maximumSpeed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "departureDate", referencedColumnName = "departureDate", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "attapId", referencedColumnName = "attapId", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "trainNumber", referencedColumnName = "trainNumber", nullable = false, insertable = false, updatable = false)})
    private TimeTableRow startTimeTableRow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "departureDate", referencedColumnName = "departureDate", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "saapAttapId", referencedColumnName = "attapId", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "trainNumber", referencedColumnName = "trainNumber", nullable = false, insertable = false, updatable = false)})
    private TimeTableRow endTimeTableRow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "departureDate", referencedColumnName = "departureDate", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "trainNumber", referencedColumnName = "trainNumber", nullable = false, insertable = false, updatable = false)})
    private Composition composition;

    @OneToMany(mappedBy = "journeySection", fetch = FetchType.LAZY)
    private Set<Wagon> wagons;

    @OneToMany(mappedBy = "journeySection", fetch = FetchType.LAZY)
    private Set<Locomotive> locomotives;

    protected JourneySection() {
    }
}
