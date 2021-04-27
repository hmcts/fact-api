package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_email")
@Getter
@Setter
@NoArgsConstructor
public class Email {
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
    private EmailType adminEmailType;

    public Email(String address, String explanation,
                 String explanationCy, EmailType emailType) {
        this.address = address;
        this.explanation = explanation;
        this.explanationCy = explanationCy;
        this.adminEmailType = emailType;
    }
}
