package fi.digitraffic.graphql.rail.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Wagon {

    @Id
    public Long id;

    public String wagonType;
    public int location;
    public int salesNumber;
    public int length;
    public Boolean playground;
    public Boolean pet;
    public Boolean catering;
    public Boolean video;
    public Boolean luggage;
    public Boolean smoking;
    public Boolean disabled;
    public String vehicleNumber;

    @Column(name = "journeysection")
    public Long journeysectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journeysectionId", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private JourneySection journeySection;

    public Wagon() {
    }
}
