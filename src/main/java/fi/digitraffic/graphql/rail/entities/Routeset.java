package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Routeset {
    @Id
    @JsonIgnore
    public Long id;

    public Long version;

    public ZonedDateTime messageTime;

    @Embedded
    public StringVirtualDepartureDateTrainId trainId;

    @Column(insertable = false, updatable = false)
    public LocalDate departureDate;

    public String routeType;

    public String clientSystem;

    public String messageId;

    @Override
    public String toString() {
        return "Routeset{" +
                "id=" + id +
                ", trainId=" + trainId +
                '}';
    }
}
