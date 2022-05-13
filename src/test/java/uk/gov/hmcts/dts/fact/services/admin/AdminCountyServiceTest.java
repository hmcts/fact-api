package uk.gov.hmcts.dts.fact.services.admin;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import uk.gov.hmcts.dts.fact.repositories.CountyRepository;


import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCountyService.class)
@SuppressWarnings("PMD.TooManyMethods")
public class AdminCountyServiceTest {
    private static final int COUNTY_ID1 = 1;
    private static final int COUNTY_ID2 = 2;
    private static final int COUNTY_ID3 = 3;
    private static final String COUNTY_NAME1 = "County 1";
    private static final String COUNTY_NAME2 = "County 2";
    private static final String COUNTY_NAME3 = "County 3";
    private static final String COUNTY_COUNTRY1 = "England";
    private static final String COUNTY_COUNTRY2 = "Scotland";
    private static final String COUNTY_COUNTRY3 = "Wales";

    private static final uk.gov.hmcts.dts.fact.entity.County COUNTY1 = new uk.gov.hmcts.dts.fact.entity.County(COUNTY_ID1, COUNTY_NAME1, COUNTY_COUNTRY1);
    private static final uk.gov.hmcts.dts.fact.entity.County COUNTY2 = new uk.gov.hmcts.dts.fact.entity.County(COUNTY_ID2, COUNTY_NAME2, COUNTY_COUNTRY2);
    private static final uk.gov.hmcts.dts.fact.entity.County COUNTY3 = new uk.gov.hmcts.dts.fact.entity.County(COUNTY_ID3, COUNTY_NAME3, COUNTY_COUNTRY3);
    private static final List<uk.gov.hmcts.dts.fact.entity.County> COUNTIES = Arrays.asList(COUNTY1, COUNTY2, COUNTY3);

    @Autowired
    private AdminCountyService adminCountyService;

    @MockBean
    private CountyRepository countyRepository;

    @Test
    void shouldReturnAllCounties() {
        when(countyRepository.findAll()).thenReturn(COUNTIES);
        final List<uk.gov.hmcts.dts.fact.model.admin.County> results = adminCountyService.getAllCounties();

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(results).hasSize(COUNTIES.size());

        softly.assertThat(results.get(0).getId()).isEqualTo(COUNTY_ID1);
        softly.assertThat(results.get(0).getName()).isEqualTo(COUNTY_NAME1);
        softly.assertThat(results.get(0).getCountry()).isEqualTo(COUNTY_COUNTRY1);

        softly.assertThat(results.get(1).getId()).isEqualTo(COUNTY_ID2);
        softly.assertThat(results.get(1).getName()).isEqualTo(COUNTY_NAME2);
        softly.assertThat(results.get(1).getCountry()).isEqualTo(COUNTY_COUNTRY2);

        softly.assertThat(results.get(2).getId()).isEqualTo(COUNTY_ID3);
        softly.assertThat(results.get(2).getName()).isEqualTo(COUNTY_NAME3);
        softly.assertThat(results.get(2).getCountry()).isEqualTo(COUNTY_COUNTRY3);

        softly.assertAll();
    }

    @Test
    void shouldRetrieveAddressTypeMap() {
        when(countyRepository.findAll()).thenReturn(COUNTIES);
        final Map<Integer, uk.gov.hmcts.dts.fact.entity.County> results = adminCountyService.getCountyMap();

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(results).hasSize(3);
        softly.assertThat(results.get(COUNTY_ID1)).isEqualTo(COUNTY1);
        softly.assertThat(results.get(COUNTY_ID2)).isEqualTo(COUNTY2);
        softly.assertThat(results.get(COUNTY_ID3)).isEqualTo(COUNTY3);
        softly.assertAll();
    }
}
