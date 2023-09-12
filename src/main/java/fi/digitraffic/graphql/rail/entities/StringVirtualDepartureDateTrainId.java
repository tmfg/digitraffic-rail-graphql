package fi.digitraffic.graphql.rail.entities;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;

public class StringVirtualDepartureDateTrainId implements Serializable {
    @Column
    public String trainNumber;

    @Column(insertable = false, updatable = false)
    public LocalDate virtualDepartureDate;

    protected StringVirtualDepartureDateTrainId() {
    }

    public StringVirtualDepartureDateTrainId(String trainNumber, LocalDate virtualDepartureDate) {
        this.trainNumber = trainNumber;
        this.virtualDepartureDate = virtualDepartureDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof StringVirtualDepartureDateTrainId)) {
            return false;
        }

        StringVirtualDepartureDateTrainId trainId = (StringVirtualDepartureDateTrainId) o;

        if (!trainNumber.equals(trainId.trainNumber)) {
            return false;
        } else if (virtualDepartureDate == null && trainId.virtualDepartureDate == null) {
            return true;
        } else if (virtualDepartureDate == null && trainId.virtualDepartureDate != null) {
            return false;
        } else if (!virtualDepartureDate.equals(trainId.virtualDepartureDate)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int hashCode() {
        int result = trainNumber.hashCode();
        if (virtualDepartureDate != null) {
            result = 31 * result + virtualDepartureDate.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", virtualDepartureDate, trainNumber);
    }
}
