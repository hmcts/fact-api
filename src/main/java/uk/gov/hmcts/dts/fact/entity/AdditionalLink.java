package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "search_additionallink")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalLink {
    @Id
    @SequenceGenerator(name = "seq-gen-al", sequenceName = "search_additionallink_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen-al")
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
