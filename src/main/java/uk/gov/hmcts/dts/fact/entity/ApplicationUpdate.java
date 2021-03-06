package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_applicationupdate")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUpdate {
    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_applicationupdate_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    private String type;
    @Column(name = "type_cy")
    private String typeCy;
    private String email;
    @Column(name = "external_link")
    private String externalLink;
    @Column(name = "external_link_desc")
    private String externalLinkDescription;
    @Column(name = "external_link_desc_cy")
    private String externalLinkDescriptionCy;
}
