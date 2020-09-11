package graphqlscope.graphql.entities;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CategoryCode {
    @Id
    public Long id;
    public String categoryCode;
    public String categoryName;
    public LocalDate validFrom;
    public LocalDate validTo;
}
