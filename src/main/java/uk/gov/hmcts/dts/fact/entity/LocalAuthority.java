package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_localauthority")
@Getter
@Setter
@NoArgsConstructor
public class LocalAuthority {

    @Id
    private Integer id;
    private String name;

    public LocalAuthority(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }
}
