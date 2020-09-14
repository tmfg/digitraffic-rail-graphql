package fi.digitraffic.graphql.rail.entities;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;

public class TrainId implements Serializable {
    @Column
    public Long trainNumber;
    @Column
    public LocalDate departureDate;

    protected TrainId() {
    }

    public TrainId(long trainNumber, LocalDate departureDate) {
        this.trainNumber = trainNumber;
        this.departureDate = departureDate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainId)) return false;

        TrainId trainId = (TrainId) o;

        if (!departureDate.equals(trainId.departureDate)) return false;
        if (!trainNumber.equals(trainId.trainNumber)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = trainNumber.hashCode();
        result = 31 * result + departureDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", departureDate, trainNumber);
    }
}
