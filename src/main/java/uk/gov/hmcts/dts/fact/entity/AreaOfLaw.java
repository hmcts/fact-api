package uk.gov.hmcts.dts.fact.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_areaoflaw")
@Data
public class AreaOfLaw {
    @Id
    @JsonIgnore
    private Integer id;
    @JsonValue
    private String name;

    public AreaOfLaw(String name) {
        this.name = name;
    }
}
