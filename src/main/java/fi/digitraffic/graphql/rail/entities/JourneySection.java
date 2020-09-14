package fi.digitraffic.graphql.rail.entities;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class JourneySection {
    @Id
    public Long id;
    @Embedded
    public TrainId trainId;

    public Long attapId;
    public Long saapAttapId;

    public int totalLength;
    public int maximumSpeed;

    protected JourneySection() {
    }
}
