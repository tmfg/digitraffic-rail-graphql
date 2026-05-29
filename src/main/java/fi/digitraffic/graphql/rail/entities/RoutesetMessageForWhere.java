package fi.digitraffic.graphql.rail.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "routeset")
/**
 * Why duplicate entity? The reason for this is, that Train has the primary key as (train_number, departure_date) with types (Long, LocalDate).
 * However routeset and trainrunningmessage has train_number as a string, because that's what we get from the integration.
 * So, for filtering to function correctly, a new set of entities (*ForWhere) has been created, having train_number as Long, allowing filtering.
 */
public class RoutesetMessageForWhere {
    @Id
    @JsonIgnore
    public Long id;

    public Long version;

    public ZonedDateTime messageTime;

    @Embedded
    public TrainId trainId;

    @Column(insertable = false, updatable = false)
    public LocalDate departureDate;

    public String routeType;

    public String clientSystem;

    public String messageId;

    @OneToMany(mappedBy = "routeset", fetch = FetchType.LAZY)
    private Set<Routesection> routesections;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "virtualDepartureDate", referencedColumnName = "departureDate", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "trainNumber", referencedColumnName = "trainNumber", nullable = false, insertable = false, updatable = false)})
    private Train train;

    @Override
    public String toString() {
        return "RoutesetForWhere{" +
                "id=" + id +
                ", trainId=" + trainId +
                '}';
    }
}
