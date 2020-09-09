package graphqlscope.graphql.entities;

import java.time.ZonedDateTime;

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
    public String commuterLineID;
    public boolean runningCurrently;
    public boolean cancelled;
    public Boolean deleted;
    public Long version;
    public ZonedDateTime timetableAcceptanceDate;
    public TimetableType timetableType;
    public String operatorShortCode;
    public Long trainCategoryId;
    public Long trainTypeId;

}
