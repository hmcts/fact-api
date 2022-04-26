package uk.gov.hmcts.dts.fact.model.admin;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.AddressType;
import uk.gov.hmcts.dts.fact.entity.Court;

import java.util.Arrays;
import java.util.List;

public class CourtAddressTest {
    private static final List<String> ADDRESS = Arrays.asList("address line 1", "address line 2");
    private static final List<String> ADDRESS_CY = Arrays.asList("address line 1 cy", "address line 2 cy");
    private static final String TOWN_NAME = "town";
    private static final String TOWN_NAME_CY = "town cy";
    private static final String POSTCODE = "postcode";
    private static final Integer ADDRESS_TYPE_ID = 10;
    private static final String DESCRIPTION = "Description";
    private static final String DESCRIPTION_CY = "Description cy";

    private static final Court COURT_ENTITY = new Court();

    @Test
    void testCreationWhenAddressTypeIsSet() {
        final AddressType addressType = new AddressType(ADDRESS_TYPE_ID, "type", null);
        final uk.gov.hmcts.dts.fact.entity.CourtAddress entity = new uk.gov.hmcts.dts.fact.entity.CourtAddress(COURT_ENTITY, addressType, ADDRESS, ADDRESS_CY, TOWN_NAME, TOWN_NAME_CY, POSTCODE, DESCRIPTION, DESCRIPTION_CY);
        final CourtAddress result = new CourtAddress(entity);

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result.getAddressTypeId()).isEqualTo(ADDRESS_TYPE_ID);
        softly.assertThat(result.getAddressLines()).containsExactlyElementsOf(ADDRESS);
        softly.assertThat(result.getAddressLinesCy()).containsExactlyElementsOf(ADDRESS_CY);
        softly.assertThat(result.getTownName()).isEqualTo(TOWN_NAME);
        softly.assertThat(result.getTownNameCy()).isEqualTo(TOWN_NAME_CY);
        softly.assertThat(result.getPostcode()).isEqualTo(POSTCODE);
        softly.assertAll();
    }

    @Test
    void testCreationWhenAddressTypeIsNotSet() {
        final uk.gov.hmcts.dts.fact.entity.CourtAddress entity = new uk.gov.hmcts.dts.fact.entity.CourtAddress(COURT_ENTITY, null, ADDRESS, ADDRESS_CY, TOWN_NAME, TOWN_NAME_CY, POSTCODE, DESCRIPTION, DESCRIPTION_CY);
        final CourtAddress result = new CourtAddress(entity);

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result.getAddressTypeId()).isNull();
        softly.assertThat(result.getAddressLines()).containsExactlyElementsOf(ADDRESS);
        softly.assertThat(result.getAddressLinesCy()).containsExactlyElementsOf(ADDRESS_CY);
        softly.assertThat(result.getTownName()).isEqualTo(TOWN_NAME);
        softly.assertThat(result.getTownNameCy()).isEqualTo(TOWN_NAME_CY);
        softly.assertThat(result.getPostcode()).isEqualTo(POSTCODE);
        softly.assertAll();
    }
}
