package fi.digitraffic.graphql.rail.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class TrainType {

    @Id
    public Long id;
    public String name;
    public Long trainCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainCategoryId", referencedColumnName = "id", updatable = false, insertable = false)
    private TrainCategory trainCategory;

    public TrainType() {
    }
}
