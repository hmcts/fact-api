package uk.gov.hmcts.dts.fact.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "search_courtaddress")
@Data
public class CourtAddress {
    @Id
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;
    @OneToOne
    @JoinColumn(name = "address_type_id")
    private AddressType addressType;
    private String address;
    private String townName;
    private String postcode;
}
