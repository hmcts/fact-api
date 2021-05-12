package uk.gov.hmcts.dts.fact.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactTest {
    private static final String TYPE = "type";
    private static final String TYPE_CY = "type cy";
    private static final String CONTACT_NUMBER = "123";
    private static final String EXPLANATION = "explanation";
    private static final String EXPLANATION_CY = "explanation cy";
    private static final int SORT_ORDER = 2;

    @Test
    void testCreation() {
        ContactType contactType = new ContactType(1, TYPE, TYPE_CY);
        Contact contact = new Contact(contactType, CONTACT_NUMBER, EXPLANATION, EXPLANATION_CY, true, SORT_ORDER);

        assertThat(contact.getContactType()).isEqualTo(contactType);
        assertThat(contact.getNumber()).isEqualTo(CONTACT_NUMBER);
        assertThat(contact.getExplanation()).isEqualTo(EXPLANATION);
        assertThat(contact.getExplanationCy()).isEqualTo(EXPLANATION_CY);
        assertThat(contact.isFax()).isEqualTo(true);
        assertThat(contact.getSortOrder()).isEqualTo(SORT_ORDER);
    }

    @Test
    void testCreationWithoutFaxAndSortOrder() {
        ContactType contactType = new ContactType(1, TYPE, TYPE_CY);
        Contact contact = new Contact(contactType, CONTACT_NUMBER, EXPLANATION, EXPLANATION_CY);

        assertThat(contact.getContactType()).isEqualTo(contactType);
        assertThat(contact.getNumber()).isEqualTo(CONTACT_NUMBER);
        assertThat(contact.getExplanation()).isEqualTo(EXPLANATION);
        assertThat(contact.getExplanationCy()).isEqualTo(EXPLANATION_CY);
        assertThat(contact.isFax()).isEqualTo(false);
        assertThat(contact.getSortOrder()).isNull();
    }
}
