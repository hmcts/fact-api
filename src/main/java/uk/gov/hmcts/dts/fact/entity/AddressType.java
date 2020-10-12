package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "search_addresstype")
@Getter
@Setter
public class AddressType {
    @Id
    private Integer id;
    private String name;
    @OneToOne(mappedBy = "addressType")
    private CourtAddress courtAddress;
}
