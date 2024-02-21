package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
