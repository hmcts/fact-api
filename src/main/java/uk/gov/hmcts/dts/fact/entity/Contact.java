package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_contact")
@Getter
@Setter
@NoArgsConstructor
public class Contact {
    @Id
    @SequenceGenerator(name = "contact-seq-gen", sequenceName = "search_contact_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact-seq-gen")
    private Integer id;
    private String number;
    private String name;
    private String nameCy;
    private String explanation;
    private String explanationCy;
    private boolean inLeaflet;
    private Integer sortOrder;

    @OneToOne()
    @JoinColumn(name = "contact_type_id")
    private ContactType contactType;

    public Contact(final ContactType contactType, final String number, final String explanation, final String explanationCy) {
        this.contactType = contactType;
        this.number = number;
        this.explanation = explanation;
        this.explanationCy = explanationCy;

        // Cater for the frontend not allowing null values
        if (this.name == null) {
            this.name = "";
        }
        if (this.nameCy == null) {
            this.nameCy = "";
        }
    }
}
