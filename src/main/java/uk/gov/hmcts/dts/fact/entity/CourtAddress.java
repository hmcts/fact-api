package uk.gov.hmcts.dts.fact.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@JsonPropertyOrder({"type", "address", "town", "postcode"})
public class CourtAddress {
    @Id
    @JsonIgnore
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "court_id")
    @JsonIgnore
    private Court court;
    @OneToOne
    @JoinColumn(name = "address_type_id")
    @JsonProperty("type")
    private AddressType addressType;
    private String address;
    @JsonProperty("town")
    private String townName;
    private String postcode;
}
