package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DetailedCategoryCode {
    @Id
    public Long id;
    public Long categoryCodeId;

    @Column(name = "detailed_category_code")
    public String code;
    @Column(name = "detailed_category_name")
    public String name;
    public LocalDate validFrom;
    public LocalDate validTo;
}
