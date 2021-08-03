package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_addresstype")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressType {
    @Id
    private Integer id;
    private String name;
    private String nameCy;
}
