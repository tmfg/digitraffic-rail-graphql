package graphqlscope.graphql.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Operator {
    @Id
    public Long id;

    public String operatorShortCode;
    public Integer operatorUicCode;
    public String operatorName;
}
