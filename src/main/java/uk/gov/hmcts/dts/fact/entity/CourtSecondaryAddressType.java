package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "search_courtsecondaryaddresstype")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourtSecondaryAddressType {

    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_courtapplicationupdate_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
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

//    private CourtSecondaryAddressType(CourtAddress courtAddress, AreaOfLaw areaOfLaw) {
//        this.address = courtAddress;
//        this.areaOfLaw = areaOfLaw;
//    }
//
//    private CourtSecondaryAddressType(CourtAddress courtAddress, CourtType courtType) {
//        this.address = courtAddress;
//        this.courtType = courtType;
//    }
}
