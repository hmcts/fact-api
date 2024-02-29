package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "search_courtsecondaryaddresstype")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourtSecondaryAddressType {

    @Id
    @SequenceGenerator(name = "seq-gen-secondary-address", sequenceName = "search_courtapplicationupdate_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen-secondary-address")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "address_id")
    private CourtAddress address;

    @OneToOne
    @JoinColumn(name = "area_of_law_id")
    private AreaOfLaw areaOfLaw;

    @OneToOne
    @JoinColumn(name = "court_type_id")
    private CourtType courtType;

    public CourtSecondaryAddressType(CourtAddress courtAddress, AreaOfLaw areaOfLaw) {
        this.address = courtAddress;
        this.areaOfLaw = areaOfLaw;
    }

    public CourtSecondaryAddressType(CourtAddress courtAddress, CourtType courtType) {
        this.address = courtAddress;
        this.courtType = courtType;
    }
}
