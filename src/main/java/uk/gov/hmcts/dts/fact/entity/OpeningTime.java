package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "search_openingtime")
@Getter
@Setter
@NoArgsConstructor
public class OpeningTime extends Element {
    @Id
    @SequenceGenerator(name = "seq-gen-opening-time", sequenceName = "search_openingtime_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen-opening-time")
    private Integer id;
    @Column(name = "type")
    private String description;
    @Column(name = "type_cy")
    private String descriptionCy;
    private String hours;
    @OneToOne()
    @JoinColumn(name = "opening_type_id")
    private OpeningType adminType;

    public OpeningTime(final String type, final String typeCy, final String hours) {
        super();
        this.description = type;
        this.descriptionCy = typeCy;
        this.hours = hours;
    }

    public OpeningTime(final OpeningType adminType, final String hours) {
        super();
        this.adminType = adminType;
        this.hours = hours;
    }
}
