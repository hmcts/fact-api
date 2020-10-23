package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_contact")
@Getter
@Setter
public class Contact {
    @Id
    private Integer id;
    private String number;
    private String name;
    private String explanation;
    private String explanationCy;
    private Integer sortOrder;
}
