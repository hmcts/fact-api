package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "admin_emailtype")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailType extends ElementType {
    @Id
    private Integer id;
    private String description;
    @Column(name = "description_cy")
    private String descriptionCy;
}
