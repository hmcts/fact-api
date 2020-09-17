package uk.gov.hmcts.dts.fact.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
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
    @JsonIgnore
    private Integer id;
    private String name;
    @OneToOne(mappedBy = "addressType")
    @JsonIgnore
    private CourtAddress courtAddress;

    @JsonValue
    public String getName() {
        return name;
    }
}
