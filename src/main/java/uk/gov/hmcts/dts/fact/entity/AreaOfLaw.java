package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_areaoflaw")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AreaOfLaw {
    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_areaoflaw_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    private String name;
    private String externalLink;
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
