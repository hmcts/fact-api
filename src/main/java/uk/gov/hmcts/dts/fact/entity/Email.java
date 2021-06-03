package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_email")
@NoArgsConstructor
@Getter
@Setter
public class Email extends Element {
    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_email_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    private String address;
    private String description;
    private String descriptionCy;
    private String explanation;
    private String explanationCy;

    @OneToOne()
    @JoinColumn(name = "admin_email_type_id")
    private EmailType adminType;

    public Email(String address, String explanation, String explanationCy, EmailType adminType) {
        super();
        this.address = address;
        // Cater for the frontend not allowing null values
        if (description == null) {
            description = "";
        }
        this.explanation = explanation;
        this.explanationCy = explanationCy;
        this.adminType = adminType;
    }
}
