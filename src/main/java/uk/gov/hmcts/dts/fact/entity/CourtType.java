package uk.gov.hmcts.dts.fact.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_courttype")
@Data
public class CourtType {
    @Id
    @JsonIgnore
    private Integer id;
    private String name;

    @JsonValue
    public String getName() {
        return name;
    }
}
