package graphqlscope.graphql.entities;


import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ThirdCategoryCode {
    @Id
    public Long id;
    public Long detailedCategoryCodeId;
    public String code;
    public String name;
    public String description;
    public LocalDate validFrom;
    public LocalDate validTo;
}
