package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "search_openingtime")
@Getter
@Setter
@NoArgsConstructor
public class OpeningTime {
    @Id()
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_openingtime_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    private String type;
    private String hours;

    public OpeningTime(final String type, final String hours) {
        this.type = type;
        this.hours = hours;
    }
}
