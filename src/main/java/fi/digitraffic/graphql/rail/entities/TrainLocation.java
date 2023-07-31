package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class TrainLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    public Long id;

    @Embedded
    public TrainLocationId trainLocationId;

    public Point location;

    public Integer speed;

    public Integer accuracy;

    @Column(updatable = false, insertable = false)
    public Long trainNumber;

    @Column(updatable = false, insertable = false)
    public LocalDate departureDate;

    @Column(updatable = false, insertable = false)
    public ZonedDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "departureDate", referencedColumnName = "departureDate", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "trainNumber", referencedColumnName = "trainNumber", nullable = false, insertable = false, updatable = false)})
    private Train train;

    @Override
    public String toString() {
        return "TrainLocation{" + "trainLocationId=" + trainLocationId + '}';
    }
}
