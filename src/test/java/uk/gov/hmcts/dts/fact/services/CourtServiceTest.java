package uk.gov.hmcts.dts.fact.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;
import uk.gov.hmcts.dts.fact.model.ServiceAreaWithCourtReferencesWithDistance;
import uk.gov.hmcts.dts.fact.model.deprecated.CourtWithDistance;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;
import uk.gov.hmcts.dts.fact.repositories.ServiceAreaRepository;
import uk.gov.hmcts.dts.fact.services.search.Search;
import uk.gov.hmcts.dts.fact.services.search.ServiceAreaSearchFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CourtService.class)
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidInstantiatingObjectsInLoops", "PMD.ExcessiveImports"})
class CourtServiceTest {

    private static final String SOME_SLUG = "some-slug";
    private static final String AREA_OF_LAW_NAME = "AreaOfLawName";
    private static final String JE2_4BA = "JE2 4BA";
    private static final String TAX = "tax";
    private static final double LAT = 52.1;
    private static final double LON = 0.7;
    private static final String LOCAL_AUTHORITY_NAME = "Suffolk County Council";
    private static final String CHILDREN = "Children";
    private static final String IMMIGRATION = "Immigration";
    private static final String EMPLOYMENT = "Employment";
    private static final String GLASGOW_TRIBUNALS_CENTRE = "Glasgow Tribunals Centre";

    @Autowired
    private CourtService courtService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private CourtWithDistanceRepository courtWithDistanceRepository;

    @MockBean
    private MapitService mapitService;

    @MockBean
    private ServiceAreaRepository serviceAreaRepository;

    @MockBean
    private ServiceAreaSearchFactory serviceAreaSearchFactory;

    @Test
    void shouldThrowSlugNotFoundException() {
        when(courtRepository.findBySlug(any())).thenReturn(empty());
        assertThrows(NotFoundException.class, () -> courtService.getCourtBySlug("some-slug"));
    }

    @Test
    void shouldReturnOldCourtObject() {
        final Court court = mock(Court.class);
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(court));
        assertThat(courtService.getCourtBySlugDeprecated(SOME_SLUG)).isInstanceOf(OldCourt.class);
    }

    @Test
    void shouldReturnCourtObject() {
        final Court court = mock(Court.class);
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(court));
        assertThat(courtService.getCourtBySlug(SOME_SLUG)).isInstanceOf(uk.gov.hmcts.dts.fact.model.Court.class);
    }

    @Test
    void shouldReturnListOfCourtReferenceObject() {
        final String query = "London";
        final Court court = mock(Court.class);

        when(courtRepository.queryBy(query)).thenReturn(singletonList(court));
        final List<CourtReference> results = courtService.getCourtByNameOrAddressOrPostcodeOrTown(query);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isInstanceOf(CourtReference.class);
    }

    @Test
    void shouldReturnListOfCourts() {
        final String query = "London";
        final Court court = mock(Court.class);

        when(courtRepository.queryBy(query)).thenReturn(singletonList(court));
        final List<CourtWithDistance> results = courtService.getCourtsByNameOrAddressOrPostcodeOrTown(query);
        assertThat(results.get(0)).isInstanceOf(CourtWithDistance.class);
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnListOfTenCourts() {

        final MapitData mapitData = mock(MapitData.class);
        when(mapitService.getMapitData(any())).thenReturn(Optional.of(mapitData));

        final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            courts.add(mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class));
        }
        when(courtWithDistanceRepository.findNearestTen(anyDouble(), anyDouble())).thenReturn(courts);

        final List<CourtWithDistance> results = courtService.getNearestCourtsByPostcode("OX1 1RZ");
        assertThat(results.size()).isEqualTo(10);
        assertThat(results.get(0)).isInstanceOf(CourtWithDistance.class);
    }

    @Test
    void shouldReturnListOfCourtsWithRelevantLocalAuthority() {
        final MapitData mapitData = mock(MapitData.class);
        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of(LOCAL_AUTHORITY_NAME));
        when(mapitService.getMapitData(JE2_4BA)).thenReturn(Optional.of(mapitData));

        final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final uk.gov.hmcts.dts.fact.entity.CourtWithDistance mock = mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class);
            final AreaOfLaw areaOfLaw = new AreaOfLaw();
            areaOfLaw.setName(AREA_OF_LAW_NAME);
            final List<AreaOfLaw> areasOfLaw = singletonList(areaOfLaw);
            when(mock.getAreasOfLaw()).thenReturn(areasOfLaw);
            courts.add(mock);
        }
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndLocalAuthority(anyDouble(), anyDouble(), anyString(), anyString())).thenReturn(
            courts);

        final List<CourtWithDistance> results = courtService.getNearestCourtsByPostcodeAndAreaOfLawAndLocalAuthority(
            JE2_4BA,
            AREA_OF_LAW_NAME
        );

        assertThat(results.size()).isEqualTo(10);
        assertThat(results.get(0)).isInstanceOf(CourtWithDistance.class);
    }

    @Test
    void shouldReturnEmptyListOfCourtsIfNoCoordinates() {

        when(mapitService.getMapitData(any())).thenReturn(empty());

        final List<CourtWithDistance> results = courtService.getNearestCourtsByPostcode("JE3 4BA");
        assertThat(results).isEmpty();
        verifyNoInteractions(courtRepository);
    }

    @Test
    void shouldFilterSearchByAreaOfLaw() {

        final MapitData mapitData = mock(MapitData.class);
        when(mapitService.getMapitData(any())).thenReturn(Optional.of(mapitData));

        final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final uk.gov.hmcts.dts.fact.entity.CourtWithDistance mock = mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class);
            final AreaOfLaw areaOfLaw = new AreaOfLaw();
            if (i % 4 == 0) {
                areaOfLaw.setName(AREA_OF_LAW_NAME);
            }
            final List<AreaOfLaw> areasOfLaw = singletonList(areaOfLaw);
            when(mock.getAreasOfLaw()).thenReturn(areasOfLaw);
            courts.add(mock);
        }
        when(courtWithDistanceRepository.findNearestTenByAreaOfLaw(anyDouble(), anyDouble(), anyString())).thenReturn(
            courts);

        final List<CourtWithDistance> results = courtService.getNearestCourtsByPostcodeAndAreaOfLaw(
            "OX2 1RZ",
            AREA_OF_LAW_NAME
        );
        assertThat(results.size()).isEqualTo(10);
        assertThat(results.get(0)).isInstanceOf(CourtWithDistance.class);
    }

    @Test
    void shouldReturnEmptyListForFilterSearchByAreaOfLawIfNoCoordinates() {

        when(mapitService.getMapitData(any())).thenReturn(empty());

        final List<CourtWithDistance> results = courtService.getNearestCourtsByPostcodeAndAreaOfLaw(
            JE2_4BA,
            AREA_OF_LAW_NAME
        );

        assertThat(results).isEmpty();
        verifyNoInteractions(courtRepository);
    }

    @Test
    void shouldReturnGlasgowTribunalOnlyForNorthernIrishPostcodeSearchWithImmigration() {
        final MapitData mapitData = mock(MapitData.class);
        when(mapitService.getMapitData(any())).thenReturn(Optional.of(mapitData));

        final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final uk.gov.hmcts.dts.fact.entity.CourtWithDistance court = mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class);
            if (i == 0) {
                when(court.getName()).thenReturn(GLASGOW_TRIBUNALS_CENTRE);
            } else {
                when(court.getName()).thenReturn("Random court " + i);
            }
            courts.add(court);
        }
        when(courtWithDistanceRepository.findNearestTenByAreaOfLaw(anyDouble(), anyDouble(), eq(IMMIGRATION))).thenReturn(courts);
        final List<CourtWithDistance> results = courtService.getNearestCourtsByPostcodeAndAreaOfLaw("BT701AH", IMMIGRATION);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo(GLASGOW_TRIBUNALS_CENTRE);
    }

    private static Stream<Arguments> parametersForPostcodeSearchWithoutLocalAuthority() {
        return Stream.of(
            // Scottish postcode
            Arguments.of("EH12DH", EMPLOYMENT, false),
            // TD area postcode in Scotland (currently TD postcode not counted as Scottish postcode)
            Arguments.of("TD13SP", EMPLOYMENT, true),
            // TD area postcode in England
            Arguments.of("TD152PS", EMPLOYMENT, true),
            // Northern Irish postcode with Immigration area of law
            Arguments.of("BT39JS", IMMIGRATION, true),
            // Northern Irish postcode with Employment area of law
            Arguments.of("BT39JS", EMPLOYMENT, false),
            // English postcode
            Arguments.of("E149DT", IMMIGRATION, true),
            // Welsh postcode
            Arguments.of("LL328DE", IMMIGRATION, true)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForPostcodeSearchWithoutLocalAuthority")
    void shouldNotUseMapitServiceForScottishOrNorthernIrishPostcodeSearch(final String postcode, final String areaOfLaw, final boolean useMapitService) {
        final List<CourtWithDistance> results = courtService.getNearestCourtsByPostcodeAndAreaOfLaw(postcode, areaOfLaw);
        if (useMapitService) {
            verify(mapitService).getMapitData(postcode);
        } else {
            verifyNoInteractions(mapitService);
            assertThat(results).isEmpty();
        }
    }

    private static Stream<Arguments> parametersForPostcodeSearchWithLocalAuthority() {
        return Stream.of(
            // Scottish postcode
            Arguments.of("EH12DH", false),
            // TD area postcode in Scotland
            Arguments.of("TD13SP", true),
            // TD area postcode in England
            Arguments.of("TD152PS", true),
            // Northern Irish postcode
            Arguments.of("BT39JS", false),
            // English postcode
            Arguments.of("E149DT", true),
            // Welsh postcode
            Arguments.of("LL328DE", true)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForPostcodeSearchWithLocalAuthority")
    void shouldNotUseMapitServiceForScottishOrNorthernIrishPostcodeSearchWithLocalAuthority(final String postcode, final boolean useMapitService) {
        final List<CourtWithDistance> results = courtService.getNearestCourtsByPostcodeAndAreaOfLawAndLocalAuthority(postcode, CHILDREN);
        if (useMapitService) {
            verify(mapitService).getMapitData(postcode);
        } else {
            verifyNoInteractions(mapitService);
            assertThat(results).isEmpty();
        }
    }

    @Test
    void shouldReturnListForNearestCourtsByPostcodeSearch() {

        final String serviceAreaSlug = TAX;
        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);
        final Search search = mock(Search.class);

        final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courts = asList(
            mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class),
            mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class));

        when(serviceArea.getSlug()).thenReturn(serviceAreaSlug);
        when(serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug)).thenReturn(Optional.of(serviceArea));
        when(mapitService.getMapitData(any())).thenReturn(Optional.of(mapitData));
        when(serviceAreaSearchFactory.getSearchFor(serviceArea, mapitData)).thenReturn(search);
        when(search.searchWith(serviceArea, mapitData, JE2_4BA)).thenReturn(courts);

        final ServiceAreaWithCourtReferencesWithDistance results = courtService.getNearestCourtsByPostcodeSearch(
            JE2_4BA,
            serviceAreaSlug
        );

        assertThat(results.getSlug()).isEqualTo(serviceAreaSlug);
        assertThat(results.getCourts().size()).isEqualTo(2);
        assertThat(results.getCourts().get(0)).isInstanceOf(CourtReferenceWithDistance.class);
    }

    @Test
    void shouldReturnEmptyListIfNoMapitdata() {

        final String serviceAreaSlug = TAX;

        when(serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug)).thenReturn(Optional.of(mock(ServiceArea.class)));
        when(mapitService.getMapitData(any())).thenReturn(empty());

        final ServiceAreaWithCourtReferencesWithDistance results = courtService.getNearestCourtsByPostcodeSearch(
            JE2_4BA,
            TAX
        );

        assertThat(results.getSlug()).isEqualTo(serviceAreaSlug);
        assertThat(results.getCourts()).isNull();
        verifyNoInteractions(serviceAreaSearchFactory);
    }

    @Test
    void shouldReturnEmptyListIfNoServiceArea() {

        final String serviceAreaSlug = TAX;

        when(serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug)).thenReturn(empty());
        when(mapitService.getMapitData(any())).thenReturn(Optional.of(mock(MapitData.class)));

        final ServiceAreaWithCourtReferencesWithDistance results = courtService.getNearestCourtsByPostcodeSearch(
            JE2_4BA,
            serviceAreaSlug
        );

        assertThat(results.getSlug()).isEqualTo(serviceAreaSlug);
        assertThat(results.getCourts()).isNull();
        verifyNoInteractions(serviceAreaSearchFactory);
    }
}
