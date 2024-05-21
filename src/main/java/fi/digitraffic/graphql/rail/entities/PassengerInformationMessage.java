package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rami_message")
public class PassengerInformationMessage {

    @EmbeddedId
    public PassengerInformationMessageId id;
    @Column(name = "created_source")
    public ZonedDateTime creationDateTime;
    public ZonedDateTime startValidity;
    public ZonedDateTime endValidity;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumns({
            @JoinColumn(name = "trainDepartureDate",
                        referencedColumnName = "departureDate",
                        nullable = false,
                        insertable = false,
                        updatable = false),
            @JoinColumn(name = "trainNumber",
                        referencedColumnName = "trainNumber",
                        nullable = false,
                        insertable = false,
                        updatable = false) })
    public Train train;
    public LocalDate trainDepartureDate;
    public Long trainNumber;
    @OneToMany(mappedBy = "message",
               fetch = FetchType.EAGER)
    public List<PassengerInformationStation> stations;
    @OneToOne(mappedBy = "message",
              fetch = FetchType.LAZY,
              optional = true)
    public PassengerInformationAudio audio;
    @OneToOne(mappedBy = "message",
              fetch = FetchType.LAZY,
              optional = true)
    public PassengerInformationVideo video;
    public ZonedDateTime deleted;

    public PassengerInformationMessage(final PassengerInformationMessageId id, final ZonedDateTime creationDateTime,
                                       final ZonedDateTime startValidity,
                                       final ZonedDateTime endValidity, final LocalDate trainDepartureDate, final Long trainNumber,
                                       final List<PassengerInformationStation> stations, final PassengerInformationAudio audio,
                                       final PassengerInformationVideo video) {
        this.id = id;
        this.creationDateTime = creationDateTime;
        this.startValidity = startValidity;
        this.endValidity = endValidity;
        this.trainDepartureDate = trainDepartureDate;
        this.trainNumber = trainNumber;
        this.stations = stations;
        this.audio = audio;
        this.video = video;
    }

    public PassengerInformationMessage() {

    }

    @Override
    public String toString() {
        return "PassengerInformationMessage{" +
                "id='" + id.id + '\'' +
                ", version=" + id.version +
                ", creationDateTime=" + creationDateTime +
                ", startValidity=" + startValidity +
                ", endValidity=" + endValidity +
                ", trainDepartureDate=" + trainDepartureDate +
                ", trainNumber=" + trainNumber +
                ", stations=" + stations +
                ", audio=" + audio +
                ", video=" + video +
                '}';
    }

}
