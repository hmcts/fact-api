package uk.gov.hmcts.dts.fact.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_openingtime")
@Data
@JsonPropertyOrder({"description", "hours"})
public class OpeningTime {
    @Id
    @JsonIgnore
    private Integer id;
    @JsonProperty("description")
    private String type;
    private String hours;
}
