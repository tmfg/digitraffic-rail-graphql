package fi.digitraffic.graphql.rail.entities;


import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ThirdCategoryCode {
    @Id
    public String oid;
    public String detailedCategoryCodeOid;
    public String code;
    public String name;
    public String description;
    public LocalDate validFrom;
    public LocalDate validTo;
}
