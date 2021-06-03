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
@Table(name = "admin_contacttype")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactType extends ElementType {
    @Id
    private Integer id;
    @Column(name = "name")
    private String description;
    @Column(name = "name_cy")
    private String descriptionCy;
}
