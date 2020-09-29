package fi.digitraffic.graphql.rail.entities;

import java.time.ZonedDateTime;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.context.annotation.Lazy;

@Entity
public class Train {
    public enum TimetableType {
        REGULAR,
        ADHOC
    }

    @EmbeddedId
    public TrainId id;
    public String commuterLineid;
    public boolean runningCurrently;
    public boolean cancelled;
    public Boolean deleted;
    public Long version;
    public ZonedDateTime timetableAcceptanceDate;
    public TimetableType timetableType;
    public String operatorShortCode;
    public Integer operatorUicCode;
    public Long trainCategoryId;
    public Long trainTypeId;

    @Lazy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operatorShortCode", referencedColumnName = "operator_short_code", updatable = false, insertable = false)
    private Operator operator;

}
