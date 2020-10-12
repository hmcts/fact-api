package uk.gov.hmcts.dts.fact.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_email")
@Data
public class CourtEmail {
    @Id
    private Integer id;
    private String address;
    private String description;
    private String explanation;
}
