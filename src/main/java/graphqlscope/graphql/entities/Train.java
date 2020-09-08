package graphqlscope.graphql.entities;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class Train {
    public enum TimetableType {
        REGULAR,
        ADHOC
    }

    @EmbeddedId
    public TrainId id;

    @Column
    public String commuterLineID;

    @Column
    public boolean runningCurrently;

    @Column
    public boolean cancelled;

    @Column
    public Boolean deleted;

    @Column
    public Long version;

    @Column
    public ZonedDateTime timetableAcceptanceDate;

    public TimetableType timetableType;

}
