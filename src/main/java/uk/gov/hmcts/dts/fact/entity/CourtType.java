package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_courttype")
@Getter
@Setter
public class CourtType {
    @Id
    private Integer id;
    private String name;
}
