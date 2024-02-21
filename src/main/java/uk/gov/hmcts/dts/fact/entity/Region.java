package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "search_region")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Region {
    @Id
    private Integer id;
    private String name;
    private String country;
}

