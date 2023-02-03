package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "admin_emailtype")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailType extends ElementType {
    @Id
    @SequenceGenerator(name = "seq-gen-email-type", sequenceName = "search_email_type_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen-email-type")
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
