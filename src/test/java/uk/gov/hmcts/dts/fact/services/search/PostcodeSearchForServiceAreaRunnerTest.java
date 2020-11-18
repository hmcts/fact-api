package uk.gov.hmcts.dts.fact.services.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.entity.ServiceAreaCourt;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

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
    private static final String DIVORCE_AREA_OF_LAW_NAME = "Divorce";
    private static final double LAT = 52.1;
    private static final double LON = 0.7;

    @Autowired
    private PostcodeSearchForServiceAreaRunner postcodeSearchForServiceAreaRunner;

    @MockBean
    private CourtWithDistanceRepository courtWithDistanceRepository;

    @Test
    void shouldReturnFamilyRegionalSearch() {

        final MapitData mapitData = mock(MapitData.class);
        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of(LOCAL_AUTHORITY_NAME));
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

        final List<CourtWithDistance> courts = singletonList(mock(CourtWithDistance.class));

        when(serviceAreaCourt.getCatchmentType()).thenReturn(REGIONAL);
        when(courtWithDistanceRepository
            .findNearestRegionalByAreaOfLawAndLocalAuthority(LAT, LON, DIVORCE_AREA_OF_LAW_NAME, LOCAL_AUTHORITY_NAME))
            .thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isInstanceOf(CourtReferenceWithDistance.class);
        verify(courtWithDistanceRepository).findNearestRegionalByAreaOfLawAndLocalAuthority(LAT, LON, DIVORCE_AREA_OF_LAW_NAME, LOCAL_AUTHORITY_NAME);
    }

    @Test
    void shouldFallbackToRegionalProximitySearchIfFamilyRegionalSearchIsEmpty() {

        final MapitData mapitData = mock(MapitData.class);
        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of(LOCAL_AUTHORITY_NAME));

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

        final List<CourtWithDistance> courts = singletonList(mock(CourtWithDistance.class));

        when(serviceAreaCourt.getCatchmentType()).thenReturn(REGIONAL);
        when(courtWithDistanceRepository
            .findNearestRegionalByAreaOfLawAndLocalAuthority(LAT, LON, DIVORCE_AREA_OF_LAW_NAME, LOCAL_AUTHORITY_NAME))
            .thenReturn(emptyList());
        when(courtWithDistanceRepository.findNearestRegionalByAreaOfLaw(LAT, LON, DIVORCE_AREA_OF_LAW_NAME)).thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isInstanceOf(CourtReferenceWithDistance.class);
        verify(courtWithDistanceRepository).findNearestRegionalByAreaOfLawAndLocalAuthority(LAT, LON, DIVORCE_AREA_OF_LAW_NAME, LOCAL_AUTHORITY_NAME);
        verify(courtWithDistanceRepository).findNearestRegionalByAreaOfLaw(LAT, LON, DIVORCE_AREA_OF_LAW_NAME);
    }

    @Test
    void shouldReturnFamilyLocalAuthoritySearch() {

        final MapitData mapitData = mock(MapitData.class);
        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of(LOCAL_AUTHORITY_NAME));

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

        final List<CourtWithDistance> courts = asList(
            mock(CourtWithDistance.class),
            mock(CourtWithDistance.class));

        when(serviceAreaCourt.getCatchmentType()).thenReturn(NATIONAL_CATCHMENT_TYPE);
        when(courtWithDistanceRepository
            .findNearestTenByAreaOfLawAndLocalAuthority(LAT, LON, AREA_OF_LAW, LOCAL_AUTHORITY_NAME))
            .thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isInstanceOf(CourtReferenceWithDistance.class);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLawAndLocalAuthority(LAT, LON, AREA_OF_LAW, LOCAL_AUTHORITY_NAME);
    }

    @Test
    void shouldFallbackToProximitySearchIfFamilyLocalAuthoritySearchIsEmpty() {

        final MapitData mapitData = mock(MapitData.class);
        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of(LOCAL_AUTHORITY_NAME));

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

        final List<CourtWithDistance> courts = asList(
            mock(CourtWithDistance.class),
            mock(CourtWithDistance.class));

        when(serviceAreaCourt.getCatchmentType()).thenReturn(NATIONAL_CATCHMENT_TYPE);
        when(courtWithDistanceRepository
            .findNearestTenByAreaOfLawAndLocalAuthority(LAT, LON, AREA_OF_LAW, LOCAL_AUTHORITY_NAME))
            .thenReturn(emptyList());
        when(courtWithDistanceRepository
            .findNearestTenByAreaOfLaw(LAT, LON, AREA_OF_LAW))
            .thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isInstanceOf(CourtReferenceWithDistance.class);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLawAndLocalAuthority(LAT, LON, AREA_OF_LAW, LOCAL_AUTHORITY_NAME);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLaw(LAT, LON, AREA_OF_LAW);
    }

    @Test
    void shouldReturnPostcodeSearchIfNoLocalAuthorityInMapitData() {

        final MapitData mapitData = mock(MapitData.class);
        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(mapitData.getLocalAuthority()).thenReturn(empty());

        final ServiceAreaCourt serviceAreaCourt = mock(ServiceAreaCourt.class);

        final ServiceArea serviceArea = new ServiceArea();
        serviceArea.setType(FAMILY.toString());
        serviceArea.setCatchmentMethod(LOCAL_AUTHORITY);
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);

        final List<CourtWithDistance> courts = asList(
            mock(CourtWithDistance.class),
            mock(CourtWithDistance.class));

        when(serviceAreaCourt.getCatchmentType()).thenReturn(NATIONAL_CATCHMENT_TYPE);
        when(courtWithDistanceRepository.findNearestTenByAreaOfLaw(LAT, LON, AREA_OF_LAW)).thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isInstanceOf(CourtReferenceWithDistance.class);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLaw(LAT, LON, AREA_OF_LAW);
    }

    @Test
    void shouldReturnPostcodeSearchIfCatchmentMethodDoesNotMatchLocalAuthority() {

        final MapitData mapitData = mock(MapitData.class);
        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of(LOCAL_AUTHORITY_NAME));

        final ServiceAreaCourt serviceAreaCourt = mock(ServiceAreaCourt.class);

        final ServiceArea serviceArea = new ServiceArea();
        serviceArea.setType(FAMILY.toString());
        serviceArea.setCatchmentMethod("Something else");
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);

        final List<CourtWithDistance> courts = asList(
            mock(CourtWithDistance.class),
            mock(CourtWithDistance.class));

        when(serviceAreaCourt.getCatchmentType()).thenReturn(NATIONAL_CATCHMENT_TYPE);
        when(courtWithDistanceRepository.findNearestTenByAreaOfLaw(LAT, LON, AREA_OF_LAW)).thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isInstanceOf(CourtReferenceWithDistance.class);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLaw(LAT, LON, AREA_OF_LAW);
    }

    @Test
    void shouldReturnCivilSearch() {

        final MapitData mapitData = mock(MapitData.class);
        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(mapitData.getLocalAuthority()).thenReturn(empty());

        final ServiceArea serviceArea = new ServiceArea();
        serviceArea.setType(CIVIL.toString());
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);

        final List<CourtWithDistance> courts = asList(
            mock(CourtWithDistance.class),
            mock(CourtWithDistance.class));

        when(courtWithDistanceRepository
            .findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4BA))
            .thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isInstanceOf(CourtReferenceWithDistance.class);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4BA);
    }

    @Test
    void shouldFallbackToProximitySearchIfCivilSearchEmpty() {

        final MapitData mapitData = mock(MapitData.class);
        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(mapitData.getLocalAuthority()).thenReturn(empty());

        final ServiceArea serviceArea = new ServiceArea();
        serviceArea.setType(CIVIL.toString());
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);

        final List<CourtWithDistance> courts = asList(
            mock(CourtWithDistance.class),
            mock(CourtWithDistance.class));

        when(courtWithDistanceRepository
            .findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4BA))
            .thenReturn(emptyList());

        when(courtWithDistanceRepository
            .findNearestTenByAreaOfLaw(LAT, LON, AREA_OF_LAW))
            .thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isInstanceOf(CourtReferenceWithDistance.class);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4BA);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLaw(LAT, LON, AREA_OF_LAW);
    }

    @Test
    void shouldReturnPostcodeSearch() {

        final MapitData mapitData = mock(MapitData.class);
        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(mapitData.getLocalAuthority()).thenReturn(empty());

        final ServiceArea serviceArea = new ServiceArea();
        serviceArea.setType(OTHER.toString());
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);

        final List<CourtWithDistance> courts = asList(
            mock(CourtWithDistance.class),
            mock(CourtWithDistance.class));

        when(courtWithDistanceRepository.findNearestTenByAreaOfLaw(LAT, LON, AREA_OF_LAW)).thenReturn(courts);

        final List<CourtReferenceWithDistance> results = postcodeSearchForServiceAreaRunner.getSearchFor(serviceArea, JE2_4BA, mapitData);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isInstanceOf(CourtReferenceWithDistance.class);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLaw(LAT, LON, AREA_OF_LAW);
    }
}