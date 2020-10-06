package fi.digitraffic.graphql.rail.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
