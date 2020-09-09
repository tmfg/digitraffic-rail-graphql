package graphqlscope.graphql.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Locomotive {

    @Id
    public Long id;
    public int location;
    public String locomotiveType;
    public String powerTypeAbbreviation;
    public String vehicleNumber;

    @ManyToOne
    @JoinColumn(name = "journeysection", nullable = false)
    @JsonIgnore
    public JourneySection journeysection;

    public Locomotive() {
    }
}
