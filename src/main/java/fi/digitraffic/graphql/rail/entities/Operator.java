package fi.digitraffic.graphql.rail.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Operator implements Serializable {
    @Id
    public Long id;

    @Column(name = "operator_short_code")
    public String shortCode;
    public Integer operatorUicCode;
    @Column(name = "operator_name")
    public String name;
}
