package uk.gov.hmcts.dts.fact.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Contact;
import uk.gov.hmcts.dts.fact.entity.ContactType;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtContact;
import uk.gov.hmcts.dts.fact.entity.CourtHistory;
import uk.gov.hmcts.dts.fact.entity.CourtOpeningTime;
import uk.gov.hmcts.dts.fact.entity.OpeningTime;
import uk.gov.hmcts.dts.fact.entity.OpeningType;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.exception.InvalidPostcodeException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithHistoricalName;
import uk.gov.hmcts.dts.fact.model.CourtWithDistance;
import uk.gov.hmcts.dts.fact.model.ServiceAreaWithCourtReferencesWithDistance;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;
import uk.gov.hmcts.dts.fact.repositories.CourtHistoryRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;
import uk.gov.hmcts.dts.fact.repositories.ServiceAreaRepository;
import uk.gov.hmcts.dts.fact.services.search.FallbackProximitySearch;
import uk.gov.hmcts.dts.fact.services.search.ProximitySearch;
import uk.gov.hmcts.dts.fact.services.search.Search;
import uk.gov.hmcts.dts.fact.services.search.ServiceAreaSearchFactory;
import uk.gov.hmcts.dts.fact.util.Action;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CourtService.class)
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidInstantiatingObjectsInLoops", "PMD.ExcessiveImports"})
class CourtServiceTest {

    private static final String SOME_SLUG = "some-slug";
    private static final String AREA_OF_LAW_NAME = "AreaOfLawName";
    private static final String JE2_4BA = "JE2 4BA";
    private static final String NN7_4EH = "NN7 4EH";
    private static final String LONDON = "London";
    private static final String TAX = "tax";
    private static final double LAT = 52.1;
    private static final double LON = 0.7;
    private static final String LOCAL_AUTHORITY_NAME = "Suffolk County Council";
    private static final String CHILDREN = "Children";
    private static final String IMMIGRATION = "Immigration";
    private static final String EMPLOYMENT = "Employment";
    private static final String GLASGOW_TRIBUNALS_CENTRE = "Glasgow Tribunals Centre";
    private static final String TEST_TYPE_IN_SEARCH_TABLE = "Test type in search table";
    private static final String TEST_TYPE_IN_ADMIN_TABLE = "Test type in admin table";
    private static final List<String> COURT_TYPE_LIST = asList("Family Court", "Tribunal");

    private static final Court FAKE_CURRENT_COURT = new Court();
    private static final String FAKE_COURT_NAME1 = "fakeCourt1";

    private static final List<CourtHistory> FAKE_COURT_HISTORIES = asList(
        new CourtHistory(
            1, 11, FAKE_COURT_NAME1, LocalDateTime.parse("2024-02-03T10:15:30"),
            LocalDateTime.parse("2007-12-03T10:15:30"), null),
        new CourtHistory(
            2, 11, FAKE_COURT_NAME1, null, null, null),
        new CourtHistory(
            3, 11, FAKE_COURT_NAME1, LocalDateTime.parse("2024-02-03T10:15:30"),
            LocalDateTime.parse("2023-12-03T11:15:30"), ""),
        new CourtHistory(
            4, 11, FAKE_COURT_NAME1, LocalDateTime.parse("2024-02-03T10:15:30"),
            LocalDateTime.parse("2024-03-03T10:15:30"), "Llys y Goron")
    );

    @Autowired
    private CourtService courtService;

    @MockitoBean
    private ProximitySearch proximitySearch;

    @MockitoBean
    private CourtRepository courtRepository;

    @MockitoBean
    private CourtWithDistanceRepository courtWithDistanceRepository;

    @MockitoBean
    private MapitService mapitService;

    @MockitoBean
    private ServiceAreaRepository serviceAreaRepository;

    @MockitoBean
    private ServiceAreaSearchFactory serviceAreaSearchFactory;

    @MockitoBean
    private FallbackProximitySearch fallbackProximitySearch;

    @MockitoBean
    private ServiceArea serviceArea;

    @MockitoBean
    private MapitData mapitData;

    @MockitoBean
    private Search search;

    @MockitoBean
    private Court court;

    @MockitoBean
    private CourtHistoryRepository courtHistoryRepository;

    @Test
    void shouldThrowSlugNotFoundException() {
        when(courtRepository.findBySlug(any())).thenReturn(empty());
        assertThrows(NotFoundException.class, () -> courtService.getCourtBySlug(SOME_SLUG));
    }

    @Test
    void shouldReturnOldCourtObject() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(court));
        assertThat(courtService.getCourtBySlugDeprecated(SOME_SLUG)).isInstanceOf(OldCourt.class);
    }

    @Test
    void shouldReturnCourtObject() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(court));
        assertThat(courtService.getCourtBySlug(SOME_SLUG)).isInstanceOf(uk.gov.hmcts.dts.fact.model.Court.class);
    }


    @Test
    void shouldReturnCourtObjectWhenSearchingByCourtType() {
        when(courtRepository.findByCourtTypesSearchIgnoreCaseInAndDisplayedIsTrueOrderByName(COURT_TYPE_LIST)).thenReturn(singletonList(court));
        final List<uk.gov.hmcts.dts.fact.model.Court> results = courtService.getCourtsByCourtTypes(COURT_TYPE_LIST);
        assertThat(results.get(0)).isInstanceOf(uk.gov.hmcts.dts.fact.model.Court.class);
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldReturnNoCourtObjectWhenSearchingByEmptyCourtType() {
        when(courtRepository.findByCourtTypesSearchIgnoreCaseInAndDisplayedIsTrueOrderByName(emptyList())).thenReturn(emptyList());
        final List<uk.gov.hmcts.dts.fact.model.Court> results = courtService.getCourtsByCourtTypes(emptyList());
        assertThat(results).isEmpty();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> parametersForTypesTests() {
        return Stream.of(
            // Opening type in opening time table only
            Arguments.of(TEST_TYPE_IN_SEARCH_TABLE, null, TEST_TYPE_IN_SEARCH_TABLE),
            // Opening type in admin type table only
            Arguments.of(null, TEST_TYPE_IN_ADMIN_TABLE, TEST_TYPE_IN_ADMIN_TABLE),
            // Opening type in both opening time and admin type tables
            Arguments.of(TEST_TYPE_IN_SEARCH_TABLE, TEST_TYPE_IN_ADMIN_TABLE, TEST_TYPE_IN_ADMIN_TABLE)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForTypesTests")
    void openingTypeInAdminTableShouldTakePrecedence(final String typeInOpeningTimeTable, final String typeInAdminTable, final String expectedType) {
        final OpeningTime openingTime = new OpeningTime();
        openingTime.setDescription(typeInOpeningTimeTable);
        if (typeInAdminTable != null) {
            openingTime.setAdminType(new OpeningType(1, typeInAdminTable, null));
        }

        final CourtOpeningTime courtOpeningTime = mock(CourtOpeningTime.class);
        when(courtOpeningTime.getOpeningTime()).thenReturn(openingTime);
        List<CourtOpeningTime> courtOpeningTimes = singletonList(courtOpeningTime);

        when(court.getCourtOpeningTimes()).thenReturn(courtOpeningTimes);
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(court));

        final uk.gov.hmcts.dts.fact.model.Court result = courtService.getCourtBySlug(SOME_SLUG);
        assertThat(result).isInstanceOf(uk.gov.hmcts.dts.fact.model.Court.class);
        final List<uk.gov.hmcts.dts.fact.model.OpeningTime> openingTimes = result.getOpeningTimes();
        assertThat(openingTimes).hasSize(1);
        assertThat(openingTimes.get(0).getType()).isEqualTo(expectedType);
    }

    @ParameterizedTest
    @MethodSource("parametersForTypesTests")
    void contactInAdminTableShouldTakePrecedence(final String typeInContactTable, final String typeInAdminTable, final String expectedType) {
        final Contact contact = new Contact();
        contact.setDescription(typeInContactTable);
        if (typeInAdminTable != null) {
            contact.setAdminType(new ContactType(1, typeInAdminTable, null));
        }

        final CourtContact courtContact = mock(CourtContact.class);
        when(courtContact.getContact()).thenReturn(contact);
        List<CourtContact> courtContacts = singletonList(courtContact);

        when(court.getCourtContacts()).thenReturn(courtContacts);
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(court));

        final uk.gov.hmcts.dts.fact.model.Court result = courtService.getCourtBySlug(SOME_SLUG);
        assertThat(result).isInstanceOf(uk.gov.hmcts.dts.fact.model.Court.class);
        final List<uk.gov.hmcts.dts.fact.model.Contact> contacts = result.getContacts();
        assertThat(contacts).hasSize(1);
        assertThat(contacts.get(0).getName()).isEqualTo(expectedType);
    }

    @Test
    void shouldReturnPostcode() {
        when(mapitService.getMapitData(JE2_4BA)).thenReturn(Optional.of(mapitData));

        final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courts = asList(
            mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class),
            mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class)
        );
        when(proximitySearch.searchWith(mapitData)).thenReturn(courts);

        List<CourtReferenceWithDistance> results = courtService.getNearestCourtReferencesByPostcode(
            JE2_4BA
        );
        assertEquals(2, results.size());
        assertThat(results.get(0)).isInstanceOf(CourtReferenceWithDistance.class);
        proximitySearch.searchWith(mapitData);
    }

    @Test
    void shouldReturnListOfCourts() {
        final String query = LONDON;

        when(courtRepository.queryBy(query,true)).thenReturn(singletonList(court));
        final List<CourtWithDistance> results = courtService.getCourtsByNameOrAddressOrPostcodeOrTown(query, true);
        assertThat(results.get(0)).isInstanceOf(CourtWithDistance.class);
        assertThat(results).hasSize(1);
    }

    @Test
    void fuzzyMatchingShouldReturnListOfCourtsUsingExactName() {
        final String query = LONDON;
        when(courtRepository.findCourtByNameAddressTownOrPartialPostcodeExactMatch(query)).thenReturn(singletonList(mock(
            Court.class)));
        final List<CourtReference> results = courtService.getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch(query);

        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isInstanceOf(CourtReference.class);

        verify(courtRepository, never()).findCourtByCourtCode(anyInt());
        verify(courtRepository, never()).findCourtByFullPostcode(query);
        verify(courtRepository, never()).findCourtByNameAddressOrTownFuzzyMatch(query);
    }

    @Test
    void shouldSearchByNameAddressOrTownFuzzyMatchUsingMisspeltName() {
        final String query = "LONDN";
        when(courtRepository.findCourtByNameAddressTownOrPartialPostcodeExactMatch(query)).thenReturn(emptyList());
        courtService.getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch(query);

        verify(courtRepository).findCourtByNameAddressOrTownFuzzyMatch(query);
        verify(courtRepository, never()).findCourtByCourtCode(anyInt());
        verify(courtRepository, never()).findCourtByFullPostcode(query);
    }

    @Test
    void fuzzyMatchingShouldSearchByCourtCodeUsingNumericInput() {
        final String query = "1234";
        final int expectedCourtCode = 1234;
        courtService.getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch(query);

        verify(courtRepository).findCourtByCourtCode(expectedCourtCode);
        verify(courtRepository, never()).findCourtByFullPostcode(query);
        verify(courtRepository, never()).findCourtByNameAddressTownOrPartialPostcodeExactMatch(query);
        verify(courtRepository, never()).findCourtByNameAddressOrTownFuzzyMatch(query);
    }

    @Test
    void fuzzyMatchingShouldSearchByPostcodeUsingFullPostcode() {
        final String query = "M60 4JH";
        courtService.getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch(query);

        verify(courtRepository).findCourtByFullPostcode(query);
        verify(courtRepository, never()).findCourtByCourtCode(anyInt());
        verify(courtRepository, never()).findCourtByNameAddressTownOrPartialPostcodeExactMatch(query);
        verify(courtRepository, never()).findCourtByNameAddressOrTownFuzzyMatch(query);
    }

    @Test
    void fuzzyMatchingShouldSearchByNameAddressOrTownUsingPartialPostcode() {
        final String query = "M60 4";
        courtService.getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch(query);

        verify(courtRepository, never()).findCourtByFullPostcode(query);
        verify(courtRepository, never()).findCourtByCourtCode(anyInt());
        verify(courtRepository).findCourtByNameAddressTownOrPartialPostcodeExactMatch("M604");
    }

    @Test
    void shouldReturnListOfTenCourts() {
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
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndLocalAuthority(
            LAT,
            LON,
            AREA_OF_LAW_NAME,
            LOCAL_AUTHORITY_NAME,
            true
        )).thenReturn(courts);
        when(fallbackProximitySearch.fallbackIfEmpty(courts, AREA_OF_LAW_NAME, true, mapitData)).thenReturn(courts);

        final List<CourtWithDistance> results = courtService.getNearestCourtsByPostcodeAndAreaOfLawAndLocalAuthority(
            JE2_4BA,
            AREA_OF_LAW_NAME,
            true);

        assertThat(results).hasSize(10);
        assertThat(results.get(0)).isInstanceOf(CourtWithDistance.class);
    }

    @Test
    void shouldReturnFallbackSearchResultsIfLocalAuthorityNameMismatch() {
        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of(LOCAL_AUTHORITY_NAME));
        when(mapitService.getMapitData(NN7_4EH)).thenReturn(Optional.of(mapitData));

        final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final uk.gov.hmcts.dts.fact.entity.CourtWithDistance mock = mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class);
            final AreaOfLaw areaOfLaw = new AreaOfLaw();
            areaOfLaw.setName(AREA_OF_LAW_NAME);
            final List<AreaOfLaw> areasOfLaw = singletonList(areaOfLaw);
            when(mock.getAreasOfLaw()).thenReturn(areasOfLaw);
            courts.add(mock);
        }
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndLocalAuthority(
            LAT,
            LON,
            AREA_OF_LAW_NAME,
            LOCAL_AUTHORITY_NAME,
            true
        )).thenReturn(emptyList());
        when(fallbackProximitySearch.fallbackIfEmpty(emptyList(), AREA_OF_LAW_NAME, true, mapitData)).thenReturn(courts);

        final List<CourtWithDistance> results = courtService.getNearestCourtsByPostcodeAndAreaOfLawAndLocalAuthority(
            NN7_4EH,
            AREA_OF_LAW_NAME,
            true);

        assertThat(results).hasSize(10);
    }

    @Test
    void shouldReturnExceptionIfNoCoordinates() {
        when(mapitService.getMapitData(any())).thenReturn(empty());

        assertThrows(InvalidPostcodeException.class, () -> {
            courtService.getNearestCourtsByPostcode("JE3 4BA");
        });
        verifyNoInteractions(courtRepository);
    }

    @Test
    void shouldFilterSearchByAreaOfLaw() {
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
        when(courtWithDistanceRepository.findNearestTenByAreaOfLaw(anyDouble(), anyDouble(), anyString(), anyBoolean())).thenReturn(
            courts);

        final List<CourtWithDistance> results = courtService.getNearestCourtsByPostcodeAndAreaOfLaw(
            "OX2 1RZ",
            AREA_OF_LAW_NAME,
            anyBoolean()
        );
        assertThat(results).hasSize(10);
        assertThat(results.get(0)).isInstanceOf(CourtWithDistance.class);
    }

    @Test
    void shouldReturnEmptyListForFilterSearchByAreaOfLawIfNoCoordinates() {
        when(mapitService.getMapitData(any())).thenReturn(empty());
        assertThrows(InvalidPostcodeException.class, () -> {
            courtService.getNearestCourtsByPostcodeAndAreaOfLaw(
                JE2_4BA,
                AREA_OF_LAW_NAME,
                true
            );
        });

        verifyNoInteractions(courtRepository);
    }

    @Test
    void shouldReturnGlasgowTribunalOnlyForNorthernIrishPostcodeSearchWithImmigration() {
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
        when(courtWithDistanceRepository.findNearestTenByAreaOfLaw(
            anyDouble(),
            anyDouble(),
            eq(IMMIGRATION),
            anyBoolean()
        )).thenReturn(courts);
        final List<CourtWithDistance> results = courtService.getNearestCourtsByPostcodeAndAreaOfLaw(
            "BT701AH",
            IMMIGRATION,
            anyBoolean()
        );

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo(GLASGOW_TRIBUNALS_CENTRE);
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
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
        if (useMapitService) {
            when(mapitService.getMapitData(anyString())).thenReturn(Optional.of(new MapitData()));
        }
        courtService.getNearestCourtsByPostcodeAndAreaOfLaw(postcode, areaOfLaw, true);
        if (useMapitService) {
            verify(mapitService).getMapitData(postcode);
        } else {
            verifyNoInteractions(mapitService);
        }
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
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
        if (useMapitService) {
            MapitData mockData = mock(MapitData.class);
            when(mockData.getLocalAuthority()).thenReturn(Optional.of(LOCAL_AUTHORITY_NAME));
            when(mapitService.getMapitData(anyString())).thenReturn(Optional.of(mockData));
        }
        courtService.getNearestCourtsByPostcodeAndAreaOfLawAndLocalAuthority(postcode, CHILDREN, true);
        if (useMapitService) {
            verify(mapitService).getMapitData(postcode);
        } else {
            verifyNoInteractions(mapitService);
        }
    }

    @Test
    void shouldReturnListForNearestCourtsByPostcodeSearch() {
        final String serviceAreaSlug = TAX;
        final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courts = asList(
            mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class),
            mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class)
        );

        when(serviceArea.getSlug()).thenReturn(serviceAreaSlug);
        when(serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug)).thenReturn(Optional.of(serviceArea));
        when(mapitService.getMapitData(any())).thenReturn(Optional.of(mapitData));
        when(serviceAreaSearchFactory.getSearchFor(serviceArea, mapitData, Action.UNDEFINED)).thenReturn(search);
        when(search.searchWith(serviceArea, mapitData, JE2_4BA, true)).thenReturn(courts);

        final ServiceAreaWithCourtReferencesWithDistance results = courtService.getNearestCourtsByPostcodeSearch(
            JE2_4BA,
            serviceAreaSlug,
            true,
            Action.UNDEFINED
        );

        assertThat(results.getSlug()).isEqualTo(serviceAreaSlug);
        assertThat(results.getCourts()).hasSize(2);
        assertThat(results.getCourts().get(0)).isInstanceOf(CourtReferenceWithDistance.class);
    }

    @Test
    void shouldReturnListForNearestCourtsByPostcodeActionAndAreaOfLawSearch() {
        final String serviceAreaSlug = "childcare-arrangements";
        final String postcode = "RM19 1SR";

        final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courts = asList(
            mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class),
            mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class)
        );

        when(serviceArea.getSlug()).thenReturn(serviceAreaSlug);
        when(serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug)).thenReturn(Optional.of(serviceArea));
        when(mapitService.getMapitData(any())).thenReturn(Optional.of(mapitData));
        when(serviceAreaSearchFactory.getSearchFor(serviceArea, mapitData, Action.NEAREST)).thenReturn(search);
        when(search.searchWith(serviceArea, mapitData, postcode, true)).thenReturn(courts);

        final ServiceAreaWithCourtReferencesWithDistance results = courtService.getNearestCourtsByPostcodeActionAndAreaOfLawSearch(
            postcode,
            serviceAreaSlug,
            Action.NEAREST,
            true
        );

        assertThat(results.getSlug()).isEqualTo(serviceAreaSlug);
        assertThat(results.getCourts()).hasSize(2);
        assertThat(results.getCourts().get(0)).isInstanceOf(CourtReferenceWithDistance.class);
    }

    @Test
    void shouldThrowNotFoundExceptionIfNoServiceAreaData() {
        final String serviceAreaSlug = "this-slug";

        when(serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug)).thenReturn(empty());
        assertThrows(NotFoundException.class, () -> courtService.getCourtBySlug(SOME_SLUG));
    }

    @Test
    void shouldThrowNotFoundExceptionIfNoMapitData() {
        final String postcode = "p0st c0d3";

        when(mapitService.getMapitData(postcode)).thenReturn(empty());
        assertThrows(NotFoundException.class, () -> courtService.getCourtBySlug(SOME_SLUG));
    }

    @Test
    void shouldReturnEmptyListIfNoMapitdataForPostcodeOnlySearch() {
        when(mapitService.getMapitData(any())).thenReturn(empty());
        List<CourtReferenceWithDistance> results = courtService.getNearestCourtReferencesByPostcode(
            JE2_4BA
        );
        assertEquals(0, results.size());
        verifyNoInteractions(serviceAreaSearchFactory);
    }

    @Test
    void shouldReturnListIfMapitdataForPostcodeOnlySearchFound() {
        when(mapitService.getMapitData(JE2_4BA)).thenReturn(Optional.of(mapitData));

        final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courts = asList(
            mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class),
            mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class)
        );
        when(proximitySearch.searchWith(mapitData)).thenReturn(courts);

        List<CourtReferenceWithDistance> results = courtService.getNearestCourtReferencesByPostcode(
            JE2_4BA
        );
        assertEquals(2, results.size());
        assertThat(results.get(0)).isInstanceOf(CourtReferenceWithDistance.class);
    }

    @Test
    void shouldReturnEmptyListIfNoMapitdata() {
        final String serviceAreaSlug = TAX;

        when(serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug)).thenReturn(Optional.of(mock(ServiceArea.class)));
        when(mapitService.getMapitData(any())).thenReturn(empty());

        final ServiceAreaWithCourtReferencesWithDistance results = courtService.getNearestCourtsByPostcodeSearch(
            JE2_4BA,
            TAX,
            true,
            Action.UNDEFINED
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
            serviceAreaSlug,
            true,
            Action.NEAREST
        );

        assertThat(results.getSlug()).isEqualTo(serviceAreaSlug);
        assertThat(results.getCourts()).isNull();
        verifyNoInteractions(serviceAreaSearchFactory);
    }

    @Test
    void shouldReturnNotFoundIfPostcodeNotExistsForMapit() {
        final String serviceAreaSlug = TAX;

        when(serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug)).thenReturn(empty());
        when(mapitService.getMapitData(any())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            courtService.getNearestCourtsByAreaOfLawSinglePointOfEntry(
                JE2_4BA,
                serviceAreaSlug,
                "money-claims",
                Action.NEAREST,
                true
            );
        });

        verifyNoInteractions(serviceAreaSearchFactory);
    }

    @Test
    void shouldReturnCourtReferenceListWhenSearchingByPrefixAndActive() {
        when(courtRepository.findCourtByNameStartingWithIgnoreCaseAndDisplayedOrderByNameAsc(anyString(), anyBoolean()))
            .thenReturn(singletonList(court));
        final List<CourtReference> results = courtService.getCourtsByPrefixAndActiveSearch("mosh kupo");
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isInstanceOf(CourtReference.class);
    }

    @Test
    void shouldReturnNearestCourtsByAreaOfLawSinglePointOfEntry() {
        final String serviceAreaSlug = CHILDREN;
        final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courts = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            final uk.gov.hmcts.dts.fact.entity.CourtWithDistance mock = mock(uk.gov.hmcts.dts.fact.entity.CourtWithDistance.class);
            List<String> areasOfLawSpoeList = new ArrayList<>();
            areasOfLawSpoeList.add(CHILDREN);
            when(mock.getAreasOfLawSpoe()).thenReturn(areasOfLawSpoeList);
            courts.add(mock);
        }

        when(serviceArea.getSlug()).thenReturn(serviceAreaSlug);
        when(serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug)).thenReturn(Optional.of(serviceArea));
        when(mapitService.getMapitData(any())).thenReturn(Optional.of(mapitData));
        when(serviceAreaSearchFactory.getSearchFor(serviceArea, mapitData, Action.DOCUMENTS)).thenReturn(search);
        when(search.searchWith(serviceArea, mapitData, JE2_4BA,true)).thenReturn(courts);

        final ServiceAreaWithCourtReferencesWithDistance results = courtService.getNearestCourtsByAreaOfLawSinglePointOfEntry(
            JE2_4BA,
            serviceAreaSlug,
            CHILDREN,
            Action.DOCUMENTS,
            true
        );

        assertThat(results.getSlug()).isEqualTo(serviceAreaSlug);
        assertThat(results.getCourts()).hasSize(1);
        assertThat(results.getCourts().get(0)).isInstanceOf(CourtReferenceWithDistance.class);
    }

    @Test
    void shouldBeAbleToGetCourtByHistoricalCourtName() {

        FAKE_CURRENT_COURT.setId(11);
        FAKE_CURRENT_COURT.setName("fakeCurrentCourt1");
        FAKE_CURRENT_COURT.setSlug("fake-current-court-1");
        FAKE_CURRENT_COURT.setDisplayed(true);
        FAKE_CURRENT_COURT.setRegionId(9);
        FAKE_CURRENT_COURT.setUpdatedAt(Timestamp.valueOf("2024-03-03 10:15:30"));

        when(courtHistoryRepository.findAllByCourtNameIgnoreCaseOrderByUpdatedAtDesc(FAKE_COURT_NAME1)).thenReturn(FAKE_COURT_HISTORIES);
        when(courtRepository.findCourtByIdAndDisplayedIsTrue(11)).thenReturn(Optional.of(FAKE_CURRENT_COURT));


        assertThat(courtService.getCourtByCourtHistoryName(FAKE_COURT_NAME1))
            .get()
            .isInstanceOf(CourtReferenceWithHistoricalName.class)
            .extracting("name", "slug", "historicalName", "displayed", "region")
            .contains("fakeCurrentCourt1", "fake-current-court-1", FAKE_COURT_NAME1, true, 9);
    }

    @Test
    void shouldThrowExceptionWhenThereIsCourtHistoryButNoCourt() {

        when(courtHistoryRepository.findAllByCourtNameIgnoreCaseOrderByUpdatedAtDesc(FAKE_COURT_NAME1)).thenReturn(FAKE_COURT_HISTORIES);
        when(courtRepository.findCourtByIdAndDisplayedIsTrue(11)).thenReturn(empty());

        assertThatThrownBy(() -> courtService.getCourtByCourtHistoryName(FAKE_COURT_NAME1))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Not found: Court History with ID: 11 does not have a corresponding active court");
    }

    @Test
    void shouldReturnEmptyCourtWhenCourtHistoryNotFound() {
        assertThat(courtService.getCourtByCourtHistoryName(FAKE_COURT_NAME1))
            .isEmpty();
    }
}
