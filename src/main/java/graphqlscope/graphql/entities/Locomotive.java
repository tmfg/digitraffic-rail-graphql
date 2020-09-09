package graphqlscope.graphql.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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

    public Locomotive() {
    }
}
