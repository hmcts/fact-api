package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "search_contact")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contact extends Element {
    private static final String FAX_ = "Fax";
    private static final String FAX_CY = "Ffacs";

    @Id
    @SequenceGenerator(name = "contact-seq-gen", sequenceName = "search_contact_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact-seq-gen")
    private Integer id;
    private String number;
    @Column(name = "name")
    private String description;
    @Column(name = "name_cy")
    private String descriptionCy;
    private String explanation;
    private String explanationCy;
    private boolean inLeaflet;
    private boolean fax;

    @OneToOne()
    @JoinColumn(name = "contact_type_id")
    private ContactType adminType;

    public Contact(final ContactType adminType, final String number, final String explanation, final String explanationCy) {
        this(adminType, number, explanation, explanationCy, false);
    }

    public Contact(final ContactType adminType, final String number, final String explanation, final String explanationCy, final boolean fax) {
        super();
        this.adminType = adminType;
        this.number = number;
        this.explanation = explanation;
        this.explanationCy = explanationCy;
        this.fax = fax;

        // Cater for the frontend not allowing null values
        if (this.description == null) {
            this.description = "";
        }
        if (this.descriptionCy == null) {
            this.descriptionCy = "";
        }
    }

    @Override
    public String getDescription(final Element element) {
        final String description = super.getDescription(this);
        if (fax) {
            if (StringUtils.isBlank(description)) {
                return FAX_;
            } else if (!FAX_.equalsIgnoreCase(description)) {
                return description + " " + FAX_.toLowerCase(Locale.getDefault());
            }
        }
        return description;
    }

    @Override
    public String getDescriptionCy(final Element element) {
        final String descriptionCy = super.getDescriptionCy(this);
        if (fax) {
            if (StringUtils.isBlank(descriptionCy)) {
                return FAX_CY;
            } else if (!FAX_CY.equalsIgnoreCase(descriptionCy)) {
                return FAX_CY + " " + descriptionCy;
            }
        }
        return descriptionCy;
    }
}
