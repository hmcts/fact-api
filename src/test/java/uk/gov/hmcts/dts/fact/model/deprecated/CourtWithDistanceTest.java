package uk.gov.hmcts.dts.fact.model.deprecated;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.AddressType;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtAddress;
import uk.gov.hmcts.dts.fact.entity.CourtDxCode;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.DxCode;
import uk.gov.hmcts.dts.fact.entity.County;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;


public class CourtWithDistanceTest {
    private static final String TEST_DX_CODE = "DX 99";

    private static final Court COURT_ENTITY = new Court();
    private static final uk.gov.hmcts.dts.fact.entity.CourtWithDistance COURT_WITH_DISTANCE_ENTITY = new uk.gov.hmcts.dts.fact.entity.CourtWithDistance();

    @BeforeAll
    static void setUp() {
        final DxCode dxCode = new DxCode();
        dxCode.setCode(TEST_DX_CODE);

        final CourtDxCode courtDxCode = new CourtDxCode();
        courtDxCode.setDxCode(dxCode);

        final CourtType courtType = new CourtType();
        courtType.setName("Crown court");

        final AddressType addressType = new AddressType();
        addressType.setName("Visit us");

        final County county = new County();
        county.setId(1);
        county.setName("Greater London");
        county.setCountry("England");

        final CourtAddress courtAddress = new CourtAddress();
        courtAddress.setAddress("1 High Street");
        courtAddress.setTownName("London");
        courtAddress.setCounty(county);
        courtAddress.setPostcode("SW1A 1AA");
        courtAddress.setAddressType(addressType);

        final AreaOfLaw areaOfLaw = new AreaOfLaw();
        areaOfLaw.setName("Adoption");

        COURT_ENTITY.setAreasOfLawSpoe(emptyList());
        COURT_ENTITY.setCourtDxCodes(singletonList(courtDxCode));
        COURT_ENTITY.setCourtTypes(singletonList(courtType));
        COURT_ENTITY.setAddresses(singletonList(courtAddress));
        COURT_ENTITY.setAreasOfLaw(singletonList(areaOfLaw));

        COURT_WITH_DISTANCE_ENTITY.setAreasOfLawSpoe(emptyList());
        COURT_WITH_DISTANCE_ENTITY.setDxCodes(singletonList(dxCode));
        COURT_WITH_DISTANCE_ENTITY.setCourtTypes(singletonList(courtType));
        COURT_WITH_DISTANCE_ENTITY.setAddresses(singletonList(courtAddress));
        COURT_WITH_DISTANCE_ENTITY.setAreasOfLaw(singletonList(areaOfLaw));
        COURT_WITH_DISTANCE_ENTITY.setDistance(10D);
    }

    @Test
    void testDxNumberDuringCreationWithCourtEntity() {
        final CourtWithDistance courtWithDistance = new CourtWithDistance(COURT_ENTITY);
        assertThat(courtWithDistance.getDxNumber()).isEqualTo(TEST_DX_CODE);
    }

    @Test
    void testDxNumberDuringCreationWithCourtWithDistanceEntity() {
        final CourtWithDistance courtWithDistance = new CourtWithDistance(COURT_WITH_DISTANCE_ENTITY);
        assertThat(courtWithDistance.getDxNumber()).isEqualTo(TEST_DX_CODE);
    }
}
