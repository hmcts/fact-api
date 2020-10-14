package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_email")
@Getter
@Setter
public class CourtEmail {
    @Id
    private Integer id;
    private String address;
    private String description;
    private String explanation;
}
