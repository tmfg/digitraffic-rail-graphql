package graphqlscope.graphql.entities;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class JourneySection {
    @Id
    public Long id;
    @Embedded
    public TrainId trainId;

    public Long beginTimeTableRowId;
    public Long endTimeTableRowId;

//    @OneToMany(mappedBy = "journeysection", fetch = FetchType.LAZY)
//    public Set<Locomotive> locomotives = new LinkedHashSet<>();
//
//    @OneToMany(mappedBy = "journeysection", fetch = FetchType.LAZY)
//    public Set<Wagon> wagons = new LinkedHashSet<>();


    public int totalLength;
    public int maximumSpeed;

    protected JourneySection() {
    }
}
