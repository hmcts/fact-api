package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_openingtime")
@Getter
@Setter
@NoArgsConstructor
public class OpeningTime extends Element {
    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_openingtime_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    @Column(name = "type")
    private String description;
    @Column(name = "type_cy")
    private String descriptionCy;
    private String hours;
    @OneToOne()
    @JoinColumn(name = "opening_type_id")
    private OpeningType openingType;

    public OpeningTime(final String type, final String typeCy, final String hours) {
        super();
        this.description = type;
        this.descriptionCy = typeCy;
        this.hours = hours;
    }

    public OpeningTime(final OpeningType openingType, final String hours) {
        super();
        this.openingType = openingType;
        this.hours = hours;
    }

    @Override
    public ElementType getAdminType() {
        return openingType;
    }
}
