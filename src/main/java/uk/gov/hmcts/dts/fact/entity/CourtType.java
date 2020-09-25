package uk.gov.hmcts.dts.fact.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_courttype")
@Data
public class CourtType {
    @Id
    private Integer id;
    private String name;
}
