package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "routeset")
public class RoutesetMessage {
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
