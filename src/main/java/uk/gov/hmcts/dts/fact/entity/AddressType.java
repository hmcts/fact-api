package uk.gov.hmcts.dts.fact.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "search_addresstype")
@Data
public class AddressType {
    @Id
    private Integer id;
    private String name;
    @OneToOne(mappedBy = "addressType")
    private CourtAddress courtAddress;
}
