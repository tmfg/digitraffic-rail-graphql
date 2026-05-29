package fi.digitraffic.graphql.rail.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
public class TrainLocationForWhere {
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
        return "TrainLocationForWhere{" + "trainLocationId=" + trainLocationId + '}';
    }
}
