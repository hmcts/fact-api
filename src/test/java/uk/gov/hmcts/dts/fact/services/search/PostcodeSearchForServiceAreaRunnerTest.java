package uk.gov.hmcts.dts.fact.services.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.entity.ServiceAreaCourt;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.CIVIL;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.FAMILY;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.OTHER;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PostcodeSearchForServiceAreaRunner.class)
class PostcodeSearchForServiceAreaRunnerTest {

    private static final String ADOPTION = "adoption";
    private static final String DIVORCE = "divorce";
    private static final String LOCAL_AUTHORITY = "local-authority";
    private static final String REGIONAL = "regional";
    private static final String JE2_4BA = "JE2 4BA";
    private static final String AREA_OF_LAW = "Adoption";
    private static final String LOCAL_AUTHORITY_NAME = "Suffolk County Council";
    private static final String NATIONAL_CATCHMENT_TYPE = "national";
    public static final String DIVORCE_AREA_OF_LAW_NAME = "Divorce";

    @Autowired
    private PostcodeSearchForServiceAreaRunner postcodeSearchForServiceAreaRunner;

    @MockBean
    private NearestRegionalByAreaOfLawAndLocalAuthoritySearch nearestRegionalByAreaOfLawAndLocalAuthoritySearch;

    @MockBean
    private NearestRegionalByAreaOfLawSearch nearestRegionalByAreaOfLawSearch;

    @MockBean
    private NearestTenByAreaOfLawAndLocalAuthoritySearch nearestTenByAreaOfLawAndLocalAuthoritySearch;

    @MockBean
    private NearestCourtsByCourtPostcodeAndAreaOfLawSearch nearestCourtsByCourtPostcodeAndAreaOfLawSearch;

    @MockBean
    private NearestCourtsByPostcodeAndAreaOfLawSearch nearestCourtsByPostcodeAndAreaOfLawSearch;
    
    @Test
    void shouldReturnFamilyRegionalSearch() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceAreaCourt serviceAreaCourt = mock(ServiceAreaCourt.class);

        final ServiceArea serviceArea = new ServiceArea();
        serviceArea.setType(FAMILY.toString());
        serviceArea.setSlug(DIVORCE);
        serviceArea.setCatchmentMethod(LOCAL_AUTHORITY);
        final List<ServiceAreaCourt> serviceAreaCourts = singletonList(serviceAreaCourt);
        serviceArea.setServiceAreaCourts(serviceAreaCourts);
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(DIVORCE_AREA_OF_LAW_NAME);
        serviceArea.setAreaOfLaw(aol);

        final List<CourtReferenceWithDistance> courts = asList(
            mock(CourtReferenceWithDistance.class),
            mock(CourtReferenceWithDistance.class));

        when(serviceAreaCourt.getCatchmentType()).thenReturn(REGIONAL);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of(LOCAL_AUTHORITY_NAME));
        when(nearestRegionalByAreaOfLawAndLocalAuthoritySearch.search(mapitData, JE2_4BA, DIVORCE_AREA_OF_LAW_NAME)).thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results).isEqualTo(courts);
    }

    @Test
    void shouldFallbackToRegionalProximitySearchIfFamilyRegionalSearchIsEmpty() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceAreaCourt serviceAreaCourt = mock(ServiceAreaCourt.class);

        final ServiceArea serviceArea = new ServiceArea();
        serviceArea.setType(FAMILY.toString());
        serviceArea.setSlug(DIVORCE);
        serviceArea.setCatchmentMethod(LOCAL_AUTHORITY);
        final List<ServiceAreaCourt> serviceAreaCourts = singletonList(serviceAreaCourt);
        serviceArea.setServiceAreaCourts(serviceAreaCourts);
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(DIVORCE_AREA_OF_LAW_NAME);
        serviceArea.setAreaOfLaw(aol);

        final List<CourtReferenceWithDistance> courts = asList(
            mock(CourtReferenceWithDistance.class),
            mock(CourtReferenceWithDistance.class));

        when(serviceAreaCourt.getCatchmentType()).thenReturn(REGIONAL);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of(LOCAL_AUTHORITY_NAME));
        when(nearestRegionalByAreaOfLawAndLocalAuthoritySearch.search(mapitData, JE2_4BA, DIVORCE_AREA_OF_LAW_NAME)).thenReturn(emptyList());
        when(nearestRegionalByAreaOfLawSearch.search(mapitData, JE2_4BA, DIVORCE_AREA_OF_LAW_NAME)).thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results).isEqualTo(courts);
        verify(nearestRegionalByAreaOfLawSearch).search(mapitData, JE2_4BA, DIVORCE_AREA_OF_LAW_NAME);
    }

    @Test
    void shouldReturnFamilyLocalAuthoritySearch() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceAreaCourt serviceAreaCourt = mock(ServiceAreaCourt.class);

        final ServiceArea serviceArea = new ServiceArea();
        serviceArea.setType(FAMILY.toString());
        serviceArea.setSlug(ADOPTION);
        serviceArea.setCatchmentMethod(LOCAL_AUTHORITY);
        final List<ServiceAreaCourt> serviceAreaCourts = singletonList(serviceAreaCourt);
        serviceArea.setServiceAreaCourts(serviceAreaCourts);
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);

        final List<CourtReferenceWithDistance> courts = asList(
            mock(CourtReferenceWithDistance.class),
            mock(CourtReferenceWithDistance.class));

        when(serviceAreaCourt.getCatchmentType()).thenReturn(NATIONAL_CATCHMENT_TYPE);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of(LOCAL_AUTHORITY_NAME));
        when(nearestTenByAreaOfLawAndLocalAuthoritySearch.search(mapitData, JE2_4BA, AREA_OF_LAW)).thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results).isEqualTo(courts);
    }

    @Test
    void shouldFallbackToProximitySearchIfFamilyLocalAuthoritySearchIsEmpty() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceAreaCourt serviceAreaCourt = mock(ServiceAreaCourt.class);

        final ServiceArea serviceArea = new ServiceArea();
        serviceArea.setType(FAMILY.toString());
        serviceArea.setSlug(ADOPTION);
        serviceArea.setCatchmentMethod(LOCAL_AUTHORITY);
        final List<ServiceAreaCourt> serviceAreaCourts = singletonList(serviceAreaCourt);
        serviceArea.setServiceAreaCourts(serviceAreaCourts);
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);

        final List<CourtReferenceWithDistance> courts = asList(
            mock(CourtReferenceWithDistance.class),
            mock(CourtReferenceWithDistance.class));

        when(serviceAreaCourt.getCatchmentType()).thenReturn(NATIONAL_CATCHMENT_TYPE);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of(LOCAL_AUTHORITY_NAME));
        when(nearestTenByAreaOfLawAndLocalAuthoritySearch.search(mapitData, JE2_4BA, AREA_OF_LAW)).thenReturn(emptyList());
        when(nearestCourtsByPostcodeAndAreaOfLawSearch.search(mapitData, JE2_4BA, AREA_OF_LAW)).thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results).isEqualTo(courts);
        verify(nearestCourtsByPostcodeAndAreaOfLawSearch).search(mapitData, JE2_4BA, AREA_OF_LAW);
    }

    @Test
    void shouldReturnPostcodeSearchIfNoLocalAuthorityInMapitData() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceAreaCourt serviceAreaCourt = mock(ServiceAreaCourt.class);

        final ServiceArea serviceArea = new ServiceArea();
        serviceArea.setType(FAMILY.toString());
        serviceArea.setSlug(ADOPTION);
        serviceArea.setCatchmentMethod(LOCAL_AUTHORITY);
        final List<ServiceAreaCourt> serviceAreaCourts = singletonList(serviceAreaCourt);
        serviceArea.setServiceAreaCourts(serviceAreaCourts);
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);

        final List<CourtReferenceWithDistance> courts = asList(
            mock(CourtReferenceWithDistance.class),
            mock(CourtReferenceWithDistance.class));

        when(serviceAreaCourt.getCatchmentType()).thenReturn(NATIONAL_CATCHMENT_TYPE);
        when(mapitData.getLocalAuthority()).thenReturn(empty());
        when(nearestCourtsByPostcodeAndAreaOfLawSearch.search(mapitData, JE2_4BA, AREA_OF_LAW)).thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results).isEqualTo(courts);
    }

    @Test
    void shouldReturnPostcodeSearchIfCatchmentMethodDoesNotMatchLocalAuthority() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceAreaCourt serviceAreaCourt = mock(ServiceAreaCourt.class);

        final ServiceArea serviceArea = new ServiceArea();
        serviceArea.setType(FAMILY.toString());
        serviceArea.setSlug(ADOPTION);
        serviceArea.setCatchmentMethod("Something else");
        final List<ServiceAreaCourt> serviceAreaCourts = singletonList(serviceAreaCourt);
        serviceArea.setServiceAreaCourts(serviceAreaCourts);
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);

        final List<CourtReferenceWithDistance> courts = asList(
            mock(CourtReferenceWithDistance.class),
            mock(CourtReferenceWithDistance.class));

        when(serviceAreaCourt.getCatchmentType()).thenReturn(NATIONAL_CATCHMENT_TYPE);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of(LOCAL_AUTHORITY_NAME));
        when(nearestCourtsByPostcodeAndAreaOfLawSearch.search(mapitData, JE2_4BA, AREA_OF_LAW)).thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results).isEqualTo(courts);
    }

    @Test
    void shouldReturnCivilSearch() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceAreaCourt serviceAreaCourt = mock(ServiceAreaCourt.class);

        final ServiceArea serviceArea = new ServiceArea();
        serviceArea.setType(CIVIL.toString());
        serviceArea.setSlug(ADOPTION);
        serviceArea.setCatchmentMethod(LOCAL_AUTHORITY);
        final List<ServiceAreaCourt> serviceAreaCourts = singletonList(serviceAreaCourt);
        serviceArea.setServiceAreaCourts(serviceAreaCourts);
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);

        final List<CourtReferenceWithDistance> courts = asList(
            mock(CourtReferenceWithDistance.class),
            mock(CourtReferenceWithDistance.class));

        when(serviceAreaCourt.getCatchmentType()).thenReturn(NATIONAL_CATCHMENT_TYPE);
        when(mapitData.getLocalAuthority()).thenReturn(empty());
        when(nearestCourtsByCourtPostcodeAndAreaOfLawSearch.search(mapitData, JE2_4BA, AREA_OF_LAW)).thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results).isEqualTo(courts);
    }

    @Test
    void shouldReturnPostcodeSearch() {

        final ServiceArea serviceArea = new ServiceArea();
        serviceArea.setType(OTHER.toString());
        serviceArea.setSlug(ADOPTION);
        serviceArea.setCatchmentMethod(LOCAL_AUTHORITY);
        serviceArea.setServiceAreaCourts(emptyList());
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);

        final MapitData mapitData = mock(MapitData.class);

        final List<CourtReferenceWithDistance> courts = asList(
            mock(CourtReferenceWithDistance.class),
            mock(CourtReferenceWithDistance.class));

        when(nearestCourtsByPostcodeAndAreaOfLawSearch.search(mapitData, JE2_4BA, AREA_OF_LAW)).thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results).isEqualTo(courts);
    }
}