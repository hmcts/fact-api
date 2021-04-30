package uk.gov.hmcts.dts.fact.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EmailTest {

    @Test
    void testEmailTypeConstructor() {
        EmailType emailType = new EmailType(1, "desc", "desc cy");
        Email email = new Email(
            "address", "expl", "expl cy", emailType);
        assertEquals(email.getAdminEmailType(), emailType);
        assertEquals(email.getExplanation(), "expl");
        assertEquals(email.getExplanationCy(), "expl cy");
        assertEquals(email.getAddress(), "address");
        assertEquals(email.getDescription(), "");
        assertNull(email.getDescriptionCy());
    }
}
