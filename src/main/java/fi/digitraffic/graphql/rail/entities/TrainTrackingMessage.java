package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
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

    public String station;
    public String nextStation;
    public String previousStation;
    public TrainRunningMessageTypeEnum type;

    @Override
    public String toString() {
        return "TrainRunningMessage{" +
                "id=" + id +
                '}';
    }


}
