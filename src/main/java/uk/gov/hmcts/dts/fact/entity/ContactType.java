package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "admin_contacttype")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactType extends ElementType {
    @Id
    @SequenceGenerator(name = "seq-gen-contact-type", sequenceName = "admin_contacttype_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen-contact-type")
    private Integer id;
    @Column(name = "name")
    private String description;
    @Column(name = "name_cy")
    private String descriptionCy;
}
