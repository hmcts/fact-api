package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_additionallink")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalLink {
    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_additionallink_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    private String url;
    private String description;
    @Column(name = "description_cy")
    private String descriptionCy;

    public AdditionalLink(final String url, final String description, final String descriptionCy) {
        this.url = url;
        this.description = description;
        this.descriptionCy = descriptionCy;
    }
}
