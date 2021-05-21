package uk.gov.hmcts.dts.fact.model.admin;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.ContactType;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactTest {
    private static final String TYPE = "type";
    private static final String TYPE_CY = "type cy";
    private static final String CONTACT_NUMBER = "123";
    private static final String EXPLANATION = "explanation";
    private static final String EXPLANATION_CY = "explanation cy";
    private static final ContactType CONTACT_TYPE = new ContactType(10, TYPE, TYPE_CY);

    @Test
    void testCreationWhenContactTypeIsSet() {
        final uk.gov.hmcts.dts.fact.entity.Contact contactEntity = new uk.gov.hmcts.dts.fact.entity.Contact(CONTACT_TYPE, CONTACT_NUMBER, EXPLANATION, EXPLANATION_CY);
        Contact contact = new Contact(contactEntity);

        assertThat(contact.getTypeId()).isEqualTo(10);
        assertThat(contact.getNumber()).isEqualTo(CONTACT_NUMBER);
        assertThat(contact.getExplanation()).isEqualTo(EXPLANATION);
        assertThat(contact.getExplanationCy()).isEqualTo(EXPLANATION_CY);
        assertThat(contact.isFax()).isEqualTo(false);
    }

    @Test
    void testCreationWhenContactTypeIsNotSet() {
        final uk.gov.hmcts.dts.fact.entity.Contact contactEntity = new uk.gov.hmcts.dts.fact.entity.Contact(null, CONTACT_NUMBER, EXPLANATION, EXPLANATION_CY, true);
        Contact contact = new Contact(contactEntity);

        assertThat(contact.getTypeId()).isNull();
        assertThat(contact.getNumber()).isEqualTo(CONTACT_NUMBER);
        assertThat(contact.getExplanation()).isEqualTo(EXPLANATION);
        assertThat(contact.getExplanationCy()).isEqualTo(EXPLANATION_CY);
        assertThat(contact.isFax()).isEqualTo(true);
    }
}
