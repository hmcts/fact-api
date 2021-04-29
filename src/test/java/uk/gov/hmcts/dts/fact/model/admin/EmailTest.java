package uk.gov.hmcts.dts.fact.model.admin;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.EmailType;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmailTest {

    private static final int EMAIL_TYPE_ID = 1;
    private static final String EMAIL_TYPE_DESCRIPTION = "desc";
    private static final String EMAIL_TYPE_DESCRIPTION_CY = "desc cy";

    private static final String EMAIL_ADDRESS = "address";
    private static final String EMAIL_EXPLANATION = "expl";
    private static final String EMAIL_EXPLANATION_CY = "expl cy";

    private static final EmailType emailType = new EmailType(
        EMAIL_TYPE_ID, EMAIL_TYPE_DESCRIPTION, EMAIL_TYPE_DESCRIPTION_CY);

    private static final uk.gov.hmcts.dts.fact.entity.Email entEmail =
        new uk.gov.hmcts.dts.fact.entity.Email(EMAIL_ADDRESS, EMAIL_EXPLANATION, EMAIL_EXPLANATION_CY, emailType);

    @Test
    void testEmailConstructorSetsEmailType() {
        Email email = new Email(entEmail);

        assertEquals(email.getAdminEmailTypeId(), emailType.getId());
        assertEquals(email.getExplanation(), "expl");
        assertEquals(email.getExplanationCy(), "expl cy");
        assertEquals(email.getAddress(), "address");
    }

    /**
     * For catering for free text stipulation on email types.
     */
    @Test
    void testEmailConstructorNoEmailTypeFound() {
        entEmail.setAdminEmailType(null);
        Email email = new Email(entEmail);

        assertEquals(email.getExplanation(), "expl");
        assertEquals(email.getExplanationCy(), "expl cy");
        assertEquals(email.getAddress(), "address");
        assertEquals(email.getAdminEmailTypeId(), 0);
    }
}
