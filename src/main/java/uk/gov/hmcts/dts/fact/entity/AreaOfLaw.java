package uk.gov.hmcts.dts.fact.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_areaoflaw")
@Data
public class AreaOfLaw {
    @Id
    private Integer id;
    private String name;
    private String externalLink;
    @Column(name = "external_link_desc")
    private String externalLinkDescription;
}
