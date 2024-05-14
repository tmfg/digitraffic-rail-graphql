package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rami_message")
public class PassengerInformationMessage {

    @Id
    public String id;
    public int version;
    @Column(name = "created_source")
    public ZonedDateTime creationDateTime;
    public ZonedDateTime startValidity;
    public ZonedDateTime endValidity;
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

    @Override
    public String toString() {
        return "PassengerInformationMessage{" +
                "id='" + id + '\'' +
                ", version=" + version +
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
