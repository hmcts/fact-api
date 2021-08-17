package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_areaoflaw")
@Getter
@Setter
@NoArgsConstructor
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
    @Column(name = "alt_name")
    private String altName;
    @Column(name = "alt_name_cy")
    private String altNameCy;
    @Column(name = "display_name")
    private String displayName;
    @Column(name = "display_name_cy")
    private String displayNameCy;
    @Column(name = "display_external_link")
    private String displayExternalLink;

    public AreaOfLaw(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
