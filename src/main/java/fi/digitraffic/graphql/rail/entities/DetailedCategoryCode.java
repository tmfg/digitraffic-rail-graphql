package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class DetailedCategoryCode {
    @Id
    public String oid;
    public String categoryCodeOid;

    @Column(name = "detailed_category_code")
    public String code;
    @Column(name = "detailed_category_name")
    public String name;
    public LocalDate validFrom;
    public LocalDate validTo;
}
