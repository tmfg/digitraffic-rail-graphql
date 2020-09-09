package graphqlscope.graphql.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TrainType {

    @Id
    public Long id;
    public String name;
    public Long trainCategoryId;

    public TrainType() {
    }
}
