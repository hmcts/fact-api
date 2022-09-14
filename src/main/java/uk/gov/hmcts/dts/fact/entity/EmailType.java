package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "admin_emailtype")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailType extends ElementType {
    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_email_type_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    private String description;
    @Column(name = "description_cy")
    private String descriptionCy;

    public EmailType(String description, String descriptionCy) {
        super();
        this.description = description;
        this.descriptionCy = descriptionCy;
    }
}
