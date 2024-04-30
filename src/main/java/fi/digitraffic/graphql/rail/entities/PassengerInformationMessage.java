package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;

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
    public ZonedDateTime creationDateTime;
    public ZonedDateTime startValidity;
    public ZonedDateTime endValidity;
    public LocalDate trainDepartureDate;
    public Long trainNumber;
    @OneToMany(mappedBy = "rami_message", fetch = FetchType.LAZY)
    public Set<PassengerInformationStation> stations;
    @OneToOne(mappedBy = "rami_message", fetch = FetchType.LAZY)
    public PassengerInformationAudio audio;
    @OneToOne(mappedBy = "rami_message", fetch = FetchType.LAZY)
    public PassengerInformationVideo video;



}
