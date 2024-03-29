package fi.digitraffic.graphql.rail.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TrainCategory {

    @Id
    public Long id;
    public String name;

    public TrainCategory() {
    }


    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof TrainCategory) {
            return ((TrainCategory) obj).id.equals(id);
        } else {
            return false;
        }
    }
}
