package uk.gov.hmcts.dts.fact.model.admin;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.AddressType;
import uk.gov.hmcts.dts.fact.entity.County;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.Region;

import java.util.Arrays;
import java.util.List;

public class CourtAddressTest {
    private static final List<String> ADDRESS = Arrays.asList("address line 1", "address line 2");
    private static final List<String> ADDRESS_CY = Arrays.asList("address line 1 cy", "address line 2 cy");
    private static final String TOWN_NAME = "town";
    private static final String TOWN_NAME_CY = "town cy";
    private static final String POSTCODE = "postcode";
    private static final Integer ADDRESS_ID = 1;
    private static final Integer ADDRESS_TYPE_ID = 10;
    private static final Integer COUNTY_ID = 1;
    private static final Integer REGION_ID = 1;

    private static final Court COURT_ENTITY = new Court();

    @Test
    void testCreationWhenAddressTypeIsSet() {
        final AddressType addressType = new AddressType(ADDRESS_TYPE_ID, "type", null);
        final County county = new County(COUNTY_ID, "County", "Engalnd");
        final Region region = new Region(REGION_ID, "North West", "England");
        final uk.gov.hmcts.dts.fact.entity.CourtAddress entity =
            new uk.gov.hmcts.dts.fact.entity.CourtAddress(
                COURT_ENTITY,
                addressType,
                ADDRESS,
                ADDRESS_CY,
                TOWN_NAME,
                TOWN_NAME_CY,
                county,
                POSTCODE,
                region
            );
        entity.setId(ADDRESS_ID);
        final CourtAddress result = new CourtAddress(entity);

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result.getId()).isEqualTo(ADDRESS_ID);
        softly.assertThat(result.getAddressTypeId()).isEqualTo(ADDRESS_TYPE_ID);
        softly.assertThat(result.getAddressLines()).containsExactlyElementsOf(ADDRESS);
        softly.assertThat(result.getAddressLinesCy()).containsExactlyElementsOf(ADDRESS_CY);
        softly.assertThat(result.getTownName()).isEqualTo(TOWN_NAME);
        softly.assertThat(result.getTownNameCy()).isEqualTo(TOWN_NAME_CY);
        softly.assertThat(result.getCountyId()).isEqualTo(COUNTY_ID);
        softly.assertThat(result.getPostcode()).isEqualTo(POSTCODE);
        softly.assertThat(result.getRegionId()).isEqualTo(REGION_ID);
        softly.assertAll();
    }

    @Test
    void testCreationWhenAddressTypeIsNotSet() {
        final County county = new County(COUNTY_ID, "County", "Engalnd");
        final Region region = new Region(REGION_ID, "North West", "England");
        final uk.gov.hmcts.dts.fact.entity.CourtAddress entity = new uk.gov.hmcts.dts.fact.entity.CourtAddress(
            COURT_ENTITY,
            null,
            ADDRESS,
            ADDRESS_CY,
            TOWN_NAME,
            TOWN_NAME_CY,
            county,
            POSTCODE,
            region
        );
        entity.setId(ADDRESS_ID);
        final CourtAddress result = new CourtAddress(entity);


        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result.getId()).isEqualTo(ADDRESS_ID);
        softly.assertThat(result.getAddressTypeId()).isNull();
        softly.assertThat(result.getAddressLines()).containsExactlyElementsOf(ADDRESS);
        softly.assertThat(result.getAddressLinesCy()).containsExactlyElementsOf(ADDRESS_CY);
        softly.assertThat(result.getTownName()).isEqualTo(TOWN_NAME);
        softly.assertThat(result.getTownNameCy()).isEqualTo(TOWN_NAME_CY);
        softly.assertThat(result.getCountyId()).isEqualTo(COUNTY_ID);
        softly.assertThat(result.getPostcode()).isEqualTo(POSTCODE);
        softly.assertThat(result.getRegionId()).isEqualTo(REGION_ID);
        softly.assertAll();
    }

    @Test
    void testCreationWhenCountyIsNotSet() {
        final Region region = new Region(REGION_ID, "North West", "England");
        final AddressType addressType = new AddressType(ADDRESS_TYPE_ID, "type", null);
        final uk.gov.hmcts.dts.fact.entity.CourtAddress entity = new uk.gov.hmcts.dts.fact.entity.CourtAddress(
            COURT_ENTITY,
            addressType,
            ADDRESS,
            ADDRESS_CY,
            TOWN_NAME,
            TOWN_NAME_CY,
            null,
            POSTCODE,
            region
        );
        entity.setId(ADDRESS_ID);
        final CourtAddress result = new CourtAddress(entity);

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result.getId()).isEqualTo(ADDRESS_ID);
        softly.assertThat(result.getAddressTypeId()).isEqualTo(ADDRESS_TYPE_ID);
        softly.assertThat(result.getAddressLines()).containsExactlyElementsOf(ADDRESS);
        softly.assertThat(result.getAddressLinesCy()).containsExactlyElementsOf(ADDRESS_CY);
        softly.assertThat(result.getTownName()).isEqualTo(TOWN_NAME);
        softly.assertThat(result.getTownNameCy()).isEqualTo(TOWN_NAME_CY);
        softly.assertThat(result.getCountyId()).isNull();
        softly.assertThat(result.getPostcode()).isEqualTo(POSTCODE);
        softly.assertThat(result.getRegionId()).isEqualTo(REGION_ID);
        softly.assertAll();
    }
}
