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
