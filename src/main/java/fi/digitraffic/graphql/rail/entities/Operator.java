package fi.digitraffic.graphql.rail.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Operator implements Serializable {
    public Long id;

    @Column(name = "operator_short_code")
    @Id
    public String shortCode;
    public Integer operatorUicCode;
    @Column(name = "operator_name")
    public String name;
}
