package fi.digitraffic.graphql.rail.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
