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
@Table(name = "admin_openingtype")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpeningType extends ElementType {
    @Id
    @SequenceGenerator(name = "seq-gen-opening-type", sequenceName = "admin_openingtype_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen-opening-type")
    private Integer id;
    @Column(name = "name")
    private String description;
    @Column(name = "name_cy")
    private String descriptionCy;
}
