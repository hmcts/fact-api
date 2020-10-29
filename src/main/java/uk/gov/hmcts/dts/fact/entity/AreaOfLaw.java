package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_areaoflaw")
@Getter
@Setter
public class AreaOfLaw {
    @Id
    private Integer id;
    private String name;
    private String externalLink;
    private String externalLinkCy;
    @Column(name = "external_link_desc")
    private String externalLinkDescription;
    @Column(name = "external_link_desc_cy")
    private String externalLinkDescriptionCy;
}
