package graphqlscope.graphql.entities;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @ManyToOne
    @JoinColumn(name = "journeysection", nullable = false)
    @JsonIgnore
    public JourneySection journeysection;

    public Wagon() {
    }
}
