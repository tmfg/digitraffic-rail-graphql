package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "departureDate", referencedColumnName = "departureDate", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "trainNumber", referencedColumnName = "trainNumber", nullable = false, insertable = false, updatable = false)})
    private Train train;

    @OneToMany(mappedBy = "routeset", fetch = FetchType.LAZY)
    private Set<Routesection> routesections;

    @Override
    public String toString() {
        return "Routeset{" +
                "id=" + id +
                ", trainId=" + trainId +
                '}';
    }
}
