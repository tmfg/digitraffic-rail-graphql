package graphqlscope.graphql.entities;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;

public class TimeTableRowId implements Serializable {

    @Column
    public Long attapId;

    @Column
    public Long trainNumber;

    @Column
    public LocalDate departureDate;

    protected TimeTableRowId() {
    }

    public TimeTableRowId(long attapId, LocalDate departureDate, long trainNumber) {
        this.trainNumber = trainNumber;
        this.departureDate = departureDate;
        this.attapId = attapId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeTableRowId)) return false;

        TimeTableRowId that = (TimeTableRowId) o;

        if (!attapId.equals(that.attapId)) return false;
        if (!departureDate.equals(that.departureDate)) return false;
        if (!trainNumber.equals(that.trainNumber)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = attapId.hashCode();
        result = 31 * result + trainNumber.hashCode();
        result = 31 * result + departureDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TimeTableRowId{" +
                "attapId=" + attapId +
                ", trainNumber=" + trainNumber +
                ", departureDate=" + departureDate +
                '}';
    }
}
