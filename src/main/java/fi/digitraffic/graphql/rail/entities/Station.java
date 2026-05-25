package fi.digitraffic.graphql.rail.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Station {
    public String name;
    @Id
    public String shortCode;
    public int uicCode;
    public String countryCode;
    public Double longitude;
    public Double latitude;

    public Long id;

    public Boolean passengerTraffic;
    public StationTypeEnum type;

    @OneToMany(mappedBy = "station",
               fetch = FetchType.LAZY)
    private List<PassengerInformationMessageStation> passengerInformationMessageStations;

}
