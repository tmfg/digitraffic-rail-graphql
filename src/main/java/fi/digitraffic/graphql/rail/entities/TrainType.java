package fi.digitraffic.graphql.rail.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
