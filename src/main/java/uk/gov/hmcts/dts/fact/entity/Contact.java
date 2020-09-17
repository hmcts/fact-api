package uk.gov.hmcts.dts.fact.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_contact")
@Data
@JsonPropertyOrder({"number", "description", "explanation"})
public class Contact {
    @Id
    @JsonIgnore
    private Integer id;
    private String number;
    @JsonProperty("description")
    private String name;
    private String explanation;
    @JsonIgnore
    private Integer sortOrder;
}
