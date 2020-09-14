package fi.digitraffic.graphql.rail.entities;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

@Entity
public class TimeTableRow {
    public enum TimeTableRowType {
        ARRIVAL,
        DEPARTURE
    }

    public enum EstimateSourceEnum {
        LIIKE_USER,
        MIKU_USER,
        LIIKE_AUTOMATIC,
        UNKNOWN,
        COMBOCALC
    }

    @EmbeddedId
    public TimeTableRowId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "departureDate", referencedColumnName = "departureDate", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "trainNumber", referencedColumnName = "trainNumber", nullable = false, insertable = false, updatable = false)})
    public Train train;


    @Column(name = "station_uic_code")
    public int stationUICCode;
    public String stationShortCode;
    public String countryCode;
    public boolean trainStopping = true;
    public Boolean commercialStop;
    public String commercialTrack;
    public boolean cancelled;
    public ZonedDateTime scheduledTime;
    public ZonedDateTime liveEstimateTime;
    public EstimateSourceEnum estimateSource;
    public Boolean unknownDelay;
    public ZonedDateTime actualTime;
    public Long differenceInMinutes;
    public TimeTableRowType type;
}
