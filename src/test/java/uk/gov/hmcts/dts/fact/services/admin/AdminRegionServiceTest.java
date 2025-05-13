package uk.gov.hmcts.dts.fact.services.admin;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.repositories.RegionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminRegionService.class)
@SuppressWarnings("PMD.TooManyMethods")
class AdminRegionServiceTest {
    private static final int REGION_ID1 = 1;
    private static final int REGION_ID2 = 2;
    private static final int REGION_ID3 = 3;
    private static final String REGION_NAME1 = "Region 1";
    private static final String REGION_NAME2 = "Region 2";
    private static final String REGION_NAME3 = "Region 3";
    private static final String REGION_COUNTRY1 = "England";
    private static final String REGION_COUNTRY2 = "Wales";
    private static final String REGION_COUNTRY3 = "Scotland";
    private static final uk.gov.hmcts.dts.fact.entity.Region REGION1 = new uk.gov.hmcts.dts.fact.entity.Region(REGION_ID1, REGION_NAME1, REGION_COUNTRY1);
    private static final uk.gov.hmcts.dts.fact.entity.Region REGION2 = new uk.gov.hmcts.dts.fact.entity.Region(REGION_ID2, REGION_NAME2, REGION_COUNTRY2);
    private static final uk.gov.hmcts.dts.fact.entity.Region REGION3 = new uk.gov.hmcts.dts.fact.entity.Region(REGION_ID3, REGION_NAME3, REGION_COUNTRY3);
    private static final List<uk.gov.hmcts.dts.fact.entity.Region> REGIONS = Arrays.asList(REGION1, REGION2, REGION3);

    @Autowired
    private AdminRegionService adminRegionService;

    @MockitoBean
    private RegionRepository regionRepository;

    @Test
    void shouldReturnAllRegions() {
        when(regionRepository.findAll()).thenReturn(REGIONS);
        final List<uk.gov.hmcts.dts.fact.model.admin.Region> results = adminRegionService.getAllRegions();

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(results).hasSize(REGIONS.size());

        softly.assertThat(results.get(0).getId()).isEqualTo(REGION_ID1);
        softly.assertThat(results.get(0).getName()).isEqualTo(REGION_NAME1);
        softly.assertThat(results.get(0).getCountry()).isEqualTo(REGION_COUNTRY1);

        softly.assertThat(results.get(1).getId()).isEqualTo(REGION_ID2);
        softly.assertThat(results.get(1).getName()).isEqualTo(REGION_NAME2);
        softly.assertThat(results.get(1).getCountry()).isEqualTo(REGION_COUNTRY2);

        softly.assertThat(results.get(2).getId()).isEqualTo(REGION_ID3);
        softly.assertThat(results.get(2).getName()).isEqualTo(REGION_NAME3);
        softly.assertThat(results.get(2).getCountry()).isEqualTo(REGION_COUNTRY3);

        softly.assertAll();
    }

    @Test
    void shouldRetrieveRegionTypeMap() {
        when(regionRepository.findAll()).thenReturn(REGIONS);
        final Map<Integer, uk.gov.hmcts.dts.fact.entity.Region> results = adminRegionService.getRegionMap();

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(results).hasSize(3);
        softly.assertThat(results.get(REGION_ID1)).isEqualTo(REGION1);
        softly.assertThat(results.get(REGION_ID2)).isEqualTo(REGION2);
        softly.assertThat(results.get(REGION_ID3)).isEqualTo(REGION3);
        softly.assertAll();
    }
}
