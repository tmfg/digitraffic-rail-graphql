package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "train_running_message")
public class TrainTrackingMessage {
    @Id
    public Long id;
    public Long version;

    @Embedded
    public StringVirtualDepartureDateTrainId trainId;

    public LocalDate departureDate;

    @Column
    public ZonedDateTime timestamp;

    public String trackSection;
    public String nextTrackSection;
    public String previousTrackSection;

    @Column(name = "station")
    public String stationShortCode;
    @Column(name = "nextStation")
    public String nextStationShortCode;
    @Column(name = "previousStation")
    public String previousStationShortCode;
    public TrainRunningMessageTypeEnum type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station", referencedColumnName = "shortCode", updatable = false, insertable = false)
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nextStation", referencedColumnName = "shortCode", updatable = false, insertable = false)
    private Station nextStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previousStation", referencedColumnName = "shortCode", updatable = false, insertable = false)
    private Station previousStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "departureDate", referencedColumnName = "departureDate", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "trainNumber", referencedColumnName = "trainNumber", nullable = false, insertable = false, updatable = false)})
    private Train train;

    @Override
    public String toString() {
        return "TrainRunningMessage{" +
                "id=" + id +
                '}';
    }


}
