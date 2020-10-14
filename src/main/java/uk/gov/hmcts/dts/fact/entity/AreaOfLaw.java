package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
    @Column(name = "external_link_desc")
    private String externalLinkDescription;

    @OneToMany(mappedBy = "areaOfLaw")
    private List<CourtAreaOfLaw> courtAreaOfLaw;

}
