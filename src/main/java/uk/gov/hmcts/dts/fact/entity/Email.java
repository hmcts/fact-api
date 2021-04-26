package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_email")
@Getter
@Setter
@NoArgsConstructor
public class Email {
    @Id
    private Integer id;
    private String address;
    private String description;
    private String descriptionCy;
    private String explanation;
    private String explanationCy;
    private int adminEmailTypeId;

    public Email(String address, String description, String descriptionCy, String explanation,
                 String explanationCy, int adminEmailTypeId) {
        this.address = address;
        this.description = description;
        this.descriptionCy = descriptionCy;
        this.explanation = explanation;
        this.explanationCy = explanationCy;
        this.adminEmailTypeId = adminEmailTypeId;
    }
}
