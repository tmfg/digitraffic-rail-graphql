package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

    @Column(updatable = false, insertable = false)
    public Long trainNumber;

    @Column(updatable = false, insertable = false)
    public LocalDate departureDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operatorShortCode", referencedColumnName = "operator_short_code", updatable = false, insertable = false)
    private Operator operator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainTypeId", referencedColumnName = "id", updatable = false, insertable = false)
    private TrainType trainType;

}
