package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_courtsecondaryaddresstype")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourtSecondaryAddressType {

    @Id
    @SequenceGenerator(name = "seq-gen-secondary-address", sequenceName = "search_courtapplicationupdate_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gens-econdary-address")
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
