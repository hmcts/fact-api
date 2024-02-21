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
