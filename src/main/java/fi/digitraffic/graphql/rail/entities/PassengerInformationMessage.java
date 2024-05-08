package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;

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
               fetch = FetchType.LAZY)
    public Set<PassengerInformationStation> stations;
    @OneToOne(mappedBy = "message",
              fetch = FetchType.LAZY,
              optional = true)
    public PassengerInformationAudio audio;
    @OneToOne(mappedBy = "message",
              fetch = FetchType.LAZY,
              optional = true)
    public PassengerInformationVideo video;

    @Query("SELECT p FROM PassengerInformationMessage p LEFT JOIN FETCH p.audio LEFT JOIN FETCH p.video")
    List<PassengerInformationMessage> findAllWithAudioAndVideo();

}
