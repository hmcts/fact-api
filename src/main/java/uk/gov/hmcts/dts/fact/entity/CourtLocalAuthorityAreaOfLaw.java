package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_courtlocalauthorityareaoflaw")
@Getter
@Setter
@NoArgsConstructor
public class CourtLocalAuthorityAreaOfLaw {

    @Id
    private Integer id;

    @OneToOne()
    @JoinColumn(name = "area_of_law_id")
    private AreaOfLaw areaOfLaw;

    @OneToOne()
    @JoinColumn(name = "court_id")
    private Court court;

    @OneToOne()
    @JoinColumn(name = "local_authority_id")
    private LocalAuthority localAuthority;

    public CourtLocalAuthorityAreaOfLaw(final AreaOfLaw areaOfLaw, final Court court, LocalAuthority localAuthority) {
        this.areaOfLaw = areaOfLaw;
        this.court = court;
        this.localAuthority = localAuthority;
    }

}
