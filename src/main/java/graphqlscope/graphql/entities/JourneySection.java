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


    public int totalLength;
    public int maximumSpeed;

    protected JourneySection() {
    }
}
