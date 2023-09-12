package fi.digitraffic.graphql.rail.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Locomotive {

    @Id
    public Long id;
    public int location;
    public String locomotiveType;
    public String powerTypeAbbreviation;
    public String vehicleNumber;

    @Column(name = "journeysection")
    public Long journeysectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journeysection", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private JourneySection journeySection;

    public Locomotive() {
    }
}
