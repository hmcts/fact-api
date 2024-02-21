package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
