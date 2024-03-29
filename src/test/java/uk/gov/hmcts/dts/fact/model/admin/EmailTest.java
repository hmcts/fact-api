package uk.gov.hmcts.dts.fact.model.admin;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.EmailType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailTest {

    private static final int EMAIL_TYPE_ID = 1;
    private static final String EMAIL_TYPE_DESCRIPTION = "desc";
    private static final String EMAIL_TYPE_DESCRIPTION_CY = "desc cy";

    private static final String EMAIL_ADDRESS = "address";
    private static final String EMAIL_EXPLANATION = "expl";
    private static final String EMAIL_EXPLANATION_CY = "expl cy";

    private static final EmailType EMAIL_TYPE = new EmailType(
        EMAIL_TYPE_ID, EMAIL_TYPE_DESCRIPTION, EMAIL_TYPE_DESCRIPTION_CY);

    private static final uk.gov.hmcts.dts.fact.entity.Email ENT_EMAIL =
        new uk.gov.hmcts.dts.fact.entity.Email(EMAIL_ADDRESS, EMAIL_EXPLANATION, EMAIL_EXPLANATION_CY, EMAIL_TYPE);

    @Test
    void testEmailConstructorSetsEmailType() {
        Email email = new Email(ENT_EMAIL);

        assertEquals(email.getAdminEmailTypeId(), EMAIL_TYPE.getId());
        assertEquals(email.getExplanation(), "expl");
        assertEquals(email.getExplanationCy(), "expl cy");
        assertEquals("address",email.getAddress());
    }

    /**
     * For catering for free text stipulation on email types.
     */
    @Test
    void testEmailConstructorNoEmailTypeFound() {
        ENT_EMAIL.setAdminType(null);
        Email email = new Email(ENT_EMAIL);

        assertEquals("expl",email.getExplanation());
        assertEquals("expl cy",email.getExplanationCy());
        assertEquals("address",email.getAddress());
        assertEquals(0,email.getAdminEmailTypeId());
    }
}
