package uk.gov.hmcts.dts.fact.model.deprecated;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.*;

import java.util.List;

import static java.util.Collections.singletonList;

public class OldCourtTest {
    private static final String DX_DESCRIPTION = "DX";
    private static final String TEST_CONTACT_DESCRIPTION = "Enquiries";
    private static final String TEST_CONTACT_EXPLANATION = "Contact explanation";
    private static final String TEST_DX_EXPLANATION = "DX explanation";
    private static final String TEST_CONTACT_NUMBER = "0203999999";
    private static final String TEST_DX_CODE = "DX 99";

    @Test
    void testCreationWithContactNumberAndDxCode() {
        final Court courtEntity = new Court();

        final Contact contactEntity = new Contact();
        contactEntity.setDescription(TEST_CONTACT_DESCRIPTION);
        contactEntity.setNumber(TEST_CONTACT_NUMBER);
        contactEntity.setExplanation(TEST_CONTACT_EXPLANATION);
        CourtContact courtContactEntity = new CourtContact();
        courtContactEntity.setContact(contactEntity);
        courtEntity.setCourtContacts(singletonList(courtContactEntity));

        final DxCode dxCodeEntity = new DxCode();
        dxCodeEntity.setCode(TEST_DX_CODE);
        dxCodeEntity.setExplanation(TEST_DX_EXPLANATION);
        CourtDxCode courtDxCodeEntity = new CourtDxCode();
        courtDxCodeEntity.setDxCode(dxCodeEntity);
        courtEntity.setCourtDxCodes(singletonList(courtDxCodeEntity));

        final OldCourt oldCourt = new OldCourt(courtEntity);
        final List<uk.gov.hmcts.dts.fact.model.Contact> contacts = oldCourt.getContacts();

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(contacts).hasSize(2);
        softly.assertThat(contacts.get(0).getName()).isEqualTo(TEST_CONTACT_DESCRIPTION);
        softly.assertThat(contacts.get(0).getNumber()).isEqualTo(TEST_CONTACT_NUMBER);
        softly.assertThat(contacts.get(0).getExplanation()).isEqualTo(TEST_CONTACT_EXPLANATION);
        softly.assertThat(contacts.get(1).getName()).isEqualTo(DX_DESCRIPTION);
        softly.assertThat(contacts.get(1).getNumber()).isEqualTo(TEST_DX_CODE);
        softly.assertThat(contacts.get(1).getExplanation()).isEqualTo(TEST_DX_EXPLANATION);
        softly.assertAll();
    }
}
