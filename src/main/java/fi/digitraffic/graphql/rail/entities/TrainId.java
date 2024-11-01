package fi.digitraffic.graphql.rail.entities;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;

public class TrainId implements Serializable {
    @Column
    public Long trainNumber;
    @Column
    public LocalDate departureDate;

    protected TrainId() {
    }

    public TrainId(final long trainNumber, final LocalDate departureDate) {
        this.trainNumber = trainNumber;
        this.departureDate = departureDate;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final TrainId trainId)) return false;

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
