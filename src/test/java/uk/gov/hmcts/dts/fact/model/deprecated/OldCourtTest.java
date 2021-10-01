package uk.gov.hmcts.dts.fact.model.deprecated;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.*;

import java.util.List;

import static java.util.Collections.emptyList;
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
        courtEntity.setAreasOfLawSpoe(emptyList());

        final Contact contactEntity = new Contact();
        contactEntity.setDescription(TEST_CONTACT_DESCRIPTION);
        contactEntity.setNumber(TEST_CONTACT_NUMBER);
        contactEntity.setExplanation(TEST_CONTACT_EXPLANATION);
        CourtContact courtContactEntity = new CourtContact();
        courtContactEntity.setContact(contactEntity);
        courtEntity.setCourtContacts(singletonList(courtContactEntity));

        final CourtType courtType = new CourtType();
        courtType.setName("Crown court");

        final AddressType addressType = new AddressType();
        addressType.setName("Visit us");

        final CourtAddress courtAddress = new CourtAddress();
        courtAddress.setAddress("1 High Street");
        courtAddress.setTownName("London");
        courtAddress.setPostcode("SW1A 1AA");
        courtAddress.setAddressType(addressType);

        final AreaOfLaw areaOfLaw = new AreaOfLaw();
        areaOfLaw.setName("Adoption");

        courtEntity.setAreasOfLawSpoe(emptyList());
        courtEntity.setCourtTypes(singletonList(courtType));
        courtEntity.setAddresses(singletonList(courtAddress));
        courtEntity.setAreasOfLaw(singletonList(areaOfLaw));

        final DxCode dxCodeEntity = new DxCode();
        dxCodeEntity.setCode(TEST_DX_CODE);
        dxCodeEntity.setExplanation(TEST_DX_EXPLANATION);
        CourtDxCode courtDxCodeEntity = new CourtDxCode();
        courtDxCodeEntity.setDxCode(dxCodeEntity);
        courtEntity.setCourtDxCodes(singletonList(courtDxCodeEntity));

        final OldCourt oldCourt = new OldCourt(courtEntity);
        oldCourt.setAreasOfLawSpoe(emptyList());
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
