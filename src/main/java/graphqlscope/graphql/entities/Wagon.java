package graphqlscope.graphql.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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

    public Wagon() {
    }
}
