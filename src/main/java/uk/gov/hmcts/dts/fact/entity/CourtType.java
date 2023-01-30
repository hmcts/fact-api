package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


@Entity
@Table(name = "search_courttype")
@Getter
@Setter
@NoArgsConstructor

public class CourtType {

    @ManyToMany(mappedBy = "courtTypes")
    private List<Court> courts;

    @Id
    private Integer id;
    private String name;
    private String search;

    public CourtType(final Integer id, final String name, final String search) {
        this.id = id;
        this.name = name;
        this.search = search;
    }



}
