package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "admin_sidebarlocation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SidebarLocation {
    @Id
    private Integer id;
    private String name;
}
