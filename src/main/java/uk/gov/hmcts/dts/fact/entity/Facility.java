package uk.gov.hmcts.dts.fact.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_facility")
@Data
public class Facility {
    @Id
    @JsonIgnore
    private Integer id;
    private String description;
    private String name;
}
