package uk.gov.hmcts.dts.fact.services.admin;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.AddressType;
import uk.gov.hmcts.dts.fact.entity.County;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.model.admin.CourtAddress;
import uk.gov.hmcts.dts.fact.model.admin.CourtSecondaryAddressType;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;
import uk.gov.hmcts.dts.fact.repositories.CourtAddressRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtSecondaryAddressTypeRepository;
import uk.gov.hmcts.dts.fact.services.MapitService;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminAddressTypeService;
import uk.gov.hmcts.dts.fact.services.validation.ValidationService;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtAddressService.class)
@SuppressWarnings("PMD.TooManyMethods")
public class AdminCourtAddressServiceTest {
    private static final String COURT_SLUG = "court-slug";
    private static final int VISIT_US_ADDRESS_TYPE_ID = 5880;
    private static final int WRITE_TO_US_ADDRESS_TYPE_ID = 5881;
    private static final int VISIT_OR_CONTACT_US_ADDRESS_TYPE_ID = 5882;
    private static final int COUNTY_ID = 1;
    private static final String COUNTY_NAME = "County";
    private static final String COUNTY_COUNTRY = "England";
    private static final String VISIT_US_ADDRESS_TYPE_NAME = "Visit us";
    private static final String WRITE_TO_US_ADDRESS_TYPE_NAME = "Write to us";
    private static final String VISIT_OR_CONTACT_US_ADDRESS_TYPE_NAME = "Visit or contact us";
    private static final String VISIT_US_ADDRESS_TYPE_NAME_CY = VISIT_US_ADDRESS_TYPE_NAME + " cy";
    private static final String WRITE_TO_US_ADDRESS_TYPE_NAME_CY = WRITE_TO_US_ADDRESS_TYPE_NAME + " cy";
    private static final String VISIT_OR_CONTACT_US_ADDRESS_TYPE_NAME_CY = VISIT_OR_CONTACT_US_ADDRESS_TYPE_NAME + " cy";

    private static final AddressType VISIT_US_ADDRESS_TYPE = new AddressType(
        VISIT_US_ADDRESS_TYPE_ID,
        VISIT_US_ADDRESS_TYPE_NAME,
        VISIT_US_ADDRESS_TYPE_NAME_CY
    );
    private static final AddressType WRITE_TO_US_ADDRESS_TYPE = new AddressType(
        WRITE_TO_US_ADDRESS_TYPE_ID,
        WRITE_TO_US_ADDRESS_TYPE_NAME,
        WRITE_TO_US_ADDRESS_TYPE_NAME_CY
    );
    private static final AddressType VISIT_OR_CONTACT_US_ADDRESS_TYPE = new AddressType(
        VISIT_OR_CONTACT_US_ADDRESS_TYPE_ID,
        VISIT_OR_CONTACT_US_ADDRESS_TYPE_NAME,
        VISIT_OR_CONTACT_US_ADDRESS_TYPE_NAME_CY
    );
    private static final County COUNTY = new County(COUNTY_ID, COUNTY_NAME, COUNTY_COUNTRY);
    private static final Map<Integer, AddressType> ADDRESS_TYPE_MAP = Map.of(
        VISIT_US_ADDRESS_TYPE_ID, VISIT_US_ADDRESS_TYPE,
        WRITE_TO_US_ADDRESS_TYPE_ID, WRITE_TO_US_ADDRESS_TYPE,
        VISIT_OR_CONTACT_US_ADDRESS_TYPE_ID, VISIT_OR_CONTACT_US_ADDRESS_TYPE
    );
    private static final List<AreaOfLaw> COURT_SECONDARY_ADDRESS_AREAS_OF_LAW = asList(
        new AreaOfLaw(
            new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(
                34_257, "Civil partnership"), false),
        new AreaOfLaw(new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(
            34_248, "Adoption"), false)
    );
    private static final List<CourtType> COURT_SECONDARY_ADDRESS_COURT_TYPES = asList(
        new CourtType(
            new uk.gov.hmcts.dts.fact.entity.CourtType(11_417, "Family Court")
        ),
        new CourtType(
            new uk.gov.hmcts.dts.fact.entity.CourtType(11_418, "Tribunal")
        )
    );
    private static final List<AreaOfLaw> COURT_SECONDARY_ADDRESS_AREAS_OF_LAW_2 = asList(
        new AreaOfLaw(
            new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(
                34_300, "a test one"), false),
        new AreaOfLaw(new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(
            34_400, "a test two"), false)
    );
    private static final List<CourtType> COURT_SECONDARY_ADDRESS_COURT_TYPES_2 = asList(
        new CourtType(
            new uk.gov.hmcts.dts.fact.entity.CourtType(11_500, "Test Court")
        ),
        new CourtType(
            new uk.gov.hmcts.dts.fact.entity.CourtType(11_600, "Test Court 2")
        )
    );
    private static final CourtSecondaryAddressType COURT_SECONDARY_ADDRESS_TYPE_LIST = new CourtSecondaryAddressType(
        COURT_SECONDARY_ADDRESS_AREAS_OF_LAW,
        COURT_SECONDARY_ADDRESS_COURT_TYPES
    );
    private static final CourtSecondaryAddressType COURT_SECONDARY_ADDRESS_TYPE_LIST_2 = new CourtSecondaryAddressType(
        COURT_SECONDARY_ADDRESS_AREAS_OF_LAW_2,
        COURT_SECONDARY_ADDRESS_COURT_TYPES_2
    );
    private static final CourtSecondaryAddressType COURT_SECONDARY_ADDRESS_TYPE_LIST_3 = new CourtSecondaryAddressType(
        emptyList(), emptyList()
    );

    private static final List<String> TEST_ADDRESS1 = singletonList("1 High Street");
    private static final List<String> TEST_ADDRESS2 = asList(
        "High Court",
        "2 Main Road"
    );
    private static final List<String> TEST_ADDRESS3 = asList(
        "Another test address",
        "Another road"
    );

    private static final List<String> TEST_ADDRESS_CY1 = emptyList();
    private static final List<String> TEST_ADDRESS_CY2 = emptyList();
    private static final List<String> TEST_ADDRESS_CY3 = emptyList();

    private static final String TEST_TOWN1 = "London";
    private static final String TEST_TOWN2 = "Manchester";
    private static final String TEST_TOWN3 = "Plymouth";
    private static final String WRITE_TO_US_POSTCODE = "EC1A 1AA";
    private static final String VISIT_US_POSTCODE = "M1 2AA";
    private static final String VISIT_OR_CONTACT_US_POSTCODE = "P7 4BR";
    private static final String PARTIAL_POSTCODE = "EC1A";

    private static final int ADDRESS_COUNT = 3;
    private static final CourtAddress WRITE_TO_US_ADDRESS = new CourtAddress(
        1,
        WRITE_TO_US_ADDRESS_TYPE_ID,
        TEST_ADDRESS1,
        TEST_ADDRESS_CY1,
        TEST_TOWN1,
        null,
        COUNTY_ID,
        WRITE_TO_US_POSTCODE,
        COURT_SECONDARY_ADDRESS_TYPE_LIST
    );
    private static final CourtAddress VISIT_US_ADDRESS = new CourtAddress(
        2,
        VISIT_US_ADDRESS_TYPE_ID,
        TEST_ADDRESS2,
        TEST_ADDRESS_CY2,
        TEST_TOWN2,
        null,
        COUNTY_ID,
        VISIT_US_POSTCODE,
        COURT_SECONDARY_ADDRESS_TYPE_LIST_2
    );
    private static final CourtAddress NO_SECONDARY_COURT_TYPE_ADDRESS = new CourtAddress(
        3,
        VISIT_OR_CONTACT_US_ADDRESS_TYPE_ID,
        TEST_ADDRESS3,
        TEST_ADDRESS_CY3,
        TEST_TOWN3,
        null,
        COUNTY_ID,
        VISIT_OR_CONTACT_US_POSTCODE,
        COURT_SECONDARY_ADDRESS_TYPE_LIST_3
    );
    private static final List<CourtAddress> EXPECTED_ADDRESSES = asList(
        WRITE_TO_US_ADDRESS, VISIT_US_ADDRESS, NO_SECONDARY_COURT_TYPE_ADDRESS);

    private static final Court MOCK_COURT = mock(Court.class);
    private static final List<uk.gov.hmcts.dts.fact.entity.CourtAddress> COURT_ADDRESSES_ENTITY = asList(
        new uk.gov.hmcts.dts.fact.entity.CourtAddress(
            MOCK_COURT,
            WRITE_TO_US_ADDRESS_TYPE,
            TEST_ADDRESS1,
            TEST_ADDRESS_CY1,
            TEST_TOWN1,
            null,
            COUNTY,
            WRITE_TO_US_POSTCODE
        ),
        new uk.gov.hmcts.dts.fact.entity.CourtAddress(
            MOCK_COURT,
            VISIT_US_ADDRESS_TYPE,
            TEST_ADDRESS2,
            TEST_ADDRESS_CY2,
            TEST_TOWN2,
            null,
            COUNTY,
            VISIT_US_POSTCODE
        ),
        new uk.gov.hmcts.dts.fact.entity.CourtAddress(
            MOCK_COURT,
            VISIT_OR_CONTACT_US_ADDRESS_TYPE,
            TEST_ADDRESS3,
            TEST_ADDRESS_CY3,
            TEST_TOWN3,
            null,
            COUNTY,
            VISIT_OR_CONTACT_US_POSTCODE
        )
    );

    private static final Double LATITUDE = 1.0;
    private static final Double LONGITUDE = 2.0;

    private static final String NOT_FOUND = "Not found: ";

    @Autowired
    private AdminCourtAddressService adminCourtAddressService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private CourtAddressRepository courtAddressRepository;

    @MockBean
    private CourtSecondaryAddressTypeRepository courtSecondaryAddressTypeRepository;

    @MockBean
    private AdminAddressTypeService adminAddressTypeService;

    @MockBean
    private AdminCountyService adminCountyService;

    @MockBean
    private AdminService adminService;

    @MockBean
    private MapitService mapitService;

    @MockBean
    private ValidationService validationService;

    @MockBean
    private AdminAuditService adminAuditService;

    @Mock
    private MapitData mapitData;

    @BeforeAll
    static void beforeAll() {
        COURT_ADDRESSES_ENTITY.get(0).setId(1);
        COURT_ADDRESSES_ENTITY.get(0).setCourtSecondaryAddressType(
            asList(
                new uk.gov.hmcts.dts.fact.entity.CourtSecondaryAddressType(
                    COURT_ADDRESSES_ENTITY.get(0),
                    new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(34_257, "Civil partnership")),
                new uk.gov.hmcts.dts.fact.entity.CourtSecondaryAddressType(
                        COURT_ADDRESSES_ENTITY.get(0),
                        new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(34_248, "Adoption")),
                new uk.gov.hmcts.dts.fact.entity.CourtSecondaryAddressType(
                        COURT_ADDRESSES_ENTITY.get(0),
                        new uk.gov.hmcts.dts.fact.entity.CourtType(11_417, "Family Court")),
                new uk.gov.hmcts.dts.fact.entity.CourtSecondaryAddressType(
                        COURT_ADDRESSES_ENTITY.get(0),
                        new uk.gov.hmcts.dts.fact.entity.CourtType(11_418, "Tribunal"))
            )
        );
        COURT_ADDRESSES_ENTITY.get(1).setId(2);
        COURT_ADDRESSES_ENTITY.get(1).setCourtSecondaryAddressType(
            asList(
                new uk.gov.hmcts.dts.fact.entity.CourtSecondaryAddressType(
                        COURT_ADDRESSES_ENTITY.get(1),
                        new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(34_300, "a test one")),
                new uk.gov.hmcts.dts.fact.entity.CourtSecondaryAddressType(
                        COURT_ADDRESSES_ENTITY.get(1),
                        new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(34_400, "a test two")),
                new uk.gov.hmcts.dts.fact.entity.CourtSecondaryAddressType(
                        COURT_ADDRESSES_ENTITY.get(1),
                        new uk.gov.hmcts.dts.fact.entity.CourtType(11_500, "Test Court")),
                new uk.gov.hmcts.dts.fact.entity.CourtSecondaryAddressType(
                        COURT_ADDRESSES_ENTITY.get(1),
                        new uk.gov.hmcts.dts.fact.entity.CourtType(11_600, "Test Court 2"))
            )
        );
        COURT_ADDRESSES_ENTITY.get(2).setId(3);
        COURT_ADDRESSES_ENTITY.get(2).setCourtSecondaryAddressType(
            emptyList()
        );
    }

    @Test
    void shouldReturnAllCourtAddresses() {
        when(MOCK_COURT.getAddresses()).thenReturn(COURT_ADDRESSES_ENTITY);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(MOCK_COURT));

        final List<CourtAddress> results = adminCourtAddressService.getCourtAddressesBySlug(COURT_SLUG);
        // Visit us, or visit/write to us are court addresses, so should appear first
        assertThat(results).hasSize(ADDRESS_COUNT);
        assertThat(results.get(0)).isEqualTo(VISIT_US_ADDRESS);
        assertThat(results.get(1)).isEqualTo(NO_SECONDARY_COURT_TYPE_ADDRESS);
        assertThat(results.get(2)).isEqualTo(WRITE_TO_US_ADDRESS);
    }

    @Test
    void shouldSortAllVisitUsAddressesFirst() {
        final List<uk.gov.hmcts.dts.fact.entity.CourtAddress> courtAddresses = asList(
            new uk.gov.hmcts.dts.fact.entity.CourtAddress(
                MOCK_COURT,
                WRITE_TO_US_ADDRESS_TYPE,
                emptyList(),
                emptyList(),
                null,
                null,
                null,
                null
            ),
            new uk.gov.hmcts.dts.fact.entity.CourtAddress(
                MOCK_COURT,
                VISIT_US_ADDRESS_TYPE,
                emptyList(),
                emptyList(),
                null,
                null,
                null,
                null
            ),
            new uk.gov.hmcts.dts.fact.entity.CourtAddress(
                MOCK_COURT,
                VISIT_OR_CONTACT_US_ADDRESS_TYPE,
                emptyList(),
                emptyList(),
                null,
                null,
                null,
                null
            ),
            new uk.gov.hmcts.dts.fact.entity.CourtAddress(
                MOCK_COURT,
                WRITE_TO_US_ADDRESS_TYPE,
                emptyList(),
                emptyList(),
                null,
                null,
                null,
                null
            ),
            new uk.gov.hmcts.dts.fact.entity.CourtAddress(
                MOCK_COURT,
                WRITE_TO_US_ADDRESS_TYPE,
                emptyList(),
                emptyList(),
                null,
                null,
                null,
                null
            ),
            new uk.gov.hmcts.dts.fact.entity.CourtAddress(
                MOCK_COURT,
                VISIT_US_ADDRESS_TYPE,
                emptyList(),
                emptyList(),
                null,
                null,
                null,
                null
            )
        );
        when(MOCK_COURT.getAddresses()).thenReturn(courtAddresses);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(MOCK_COURT));

        final List<CourtAddress> results = adminCourtAddressService.getCourtAddressesBySlug(COURT_SLUG);
        assertThat(results).hasSize(courtAddresses.size());
        assertThat(results.subList(0, 3).stream().map(CourtAddress::getAddressTypeId))
            .allMatch(c -> c.equals(VISIT_US_ADDRESS_TYPE_ID) || c.equals(VISIT_OR_CONTACT_US_ADDRESS_TYPE_ID));
        assertThat(results.subList(3, 6).stream().map(CourtAddress::getAddressTypeId))
            .allMatch(c -> c.equals(WRITE_TO_US_ADDRESS_TYPE_ID));
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingAddressesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtAddressService.getCourtAddressesBySlug(COURT_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }

    @Test
    void shouldUpdateCourtAddressesForInPersonCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(MOCK_COURT));
        when(adminAddressTypeService.getAddressTypeMap()).thenReturn(Map.of(
            VISIT_US_ADDRESS_TYPE_ID,
            VISIT_US_ADDRESS_TYPE,
            WRITE_TO_US_ADDRESS_TYPE_ID,
            WRITE_TO_US_ADDRESS_TYPE,
            VISIT_OR_CONTACT_US_ADDRESS_TYPE_ID, VISIT_OR_CONTACT_US_ADDRESS_TYPE
        ));
        doAnswer(i -> i.getArguments()[0])
            .when(courtSecondaryAddressTypeRepository)
            .saveAll(anyList());
        when(adminCountyService.getCountyMap()).thenReturn(Map.of(COUNTY_ID, COUNTY));
        when(courtAddressRepository.saveAll(any())).thenReturn(COURT_ADDRESSES_ENTITY);

        when(mapitService.getMapitData(VISIT_US_POSTCODE)).thenReturn(Optional.of(mapitData));
        when(mapitData.getLat()).thenReturn(LATITUDE);
        when(mapitData.getLon()).thenReturn(LONGITUDE);

        when(MOCK_COURT.isInPerson()).thenReturn(true);

        final List<CourtAddress> results = adminCourtAddressService.updateCourtAddressesAndCoordinates(
            COURT_SLUG,
            EXPECTED_ADDRESSES
        );
        assertThat(results).hasSize(ADDRESS_COUNT);
        assertThat(results.get(0)).isEqualTo(VISIT_US_ADDRESS);
        assertThat(results.get(1)).isEqualTo(NO_SECONDARY_COURT_TYPE_ADDRESS);
        assertThat(results.get(2)).isEqualTo(WRITE_TO_US_ADDRESS);

        verify(courtAddressRepository).deleteAll(any());
        verify(courtSecondaryAddressTypeRepository, atMostOnce()).deleteAllByAddressIdIn(any());
        verify(courtSecondaryAddressTypeRepository, atMostOnce()).deleteAll(any());
        verify(courtSecondaryAddressTypeRepository, atMostOnce()).saveAll(any());
        verify(adminService).updateCourtLatLon(COURT_SLUG, LATITUDE, LONGITUDE);
        verify(adminAuditService, atLeastOnce()).saveAudit("Update court addresses and coordinates",
                                                           EXPECTED_ADDRESSES,
                                                           results, COURT_SLUG
        );
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingAddressesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());
        when(adminAddressTypeService.getAddressTypeMap()).thenReturn(Map.of(
            VISIT_US_ADDRESS_TYPE_ID,
            VISIT_US_ADDRESS_TYPE,
            WRITE_TO_US_ADDRESS_TYPE_ID,
            WRITE_TO_US_ADDRESS_TYPE,
            VISIT_OR_CONTACT_US_ADDRESS_TYPE_ID, VISIT_OR_CONTACT_US_ADDRESS_TYPE
        ));
        doAnswer(i -> i.getArguments()[0])
            .when(courtSecondaryAddressTypeRepository)
            .saveAll(anyList());

        assertThatThrownBy(() -> adminCourtAddressService.updateCourtAddressesAndCoordinates(
            COURT_SLUG,
            EXPECTED_ADDRESSES
        ))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldNotUpdateCoordinatesForNotInPersonCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(MOCK_COURT));
        when(adminAddressTypeService.getAddressTypeMap()).thenReturn(Map.of(
            VISIT_US_ADDRESS_TYPE_ID, VISIT_US_ADDRESS_TYPE,
            WRITE_TO_US_ADDRESS_TYPE_ID, WRITE_TO_US_ADDRESS_TYPE,
            VISIT_OR_CONTACT_US_ADDRESS_TYPE_ID, VISIT_OR_CONTACT_US_ADDRESS_TYPE
        ));
        doAnswer(i -> i.getArguments()[0])
            .when(courtSecondaryAddressTypeRepository)
            .saveAll(anyList());
        when(courtAddressRepository.saveAll(any())).thenReturn(COURT_ADDRESSES_ENTITY);
        when(mapitService.getMapitData(VISIT_US_POSTCODE)).thenReturn(Optional.empty());

        when(MOCK_COURT.isInPerson()).thenReturn(false);

        final List<CourtAddress> results = adminCourtAddressService.updateCourtAddressesAndCoordinates(
            COURT_SLUG,
            EXPECTED_ADDRESSES
        );
        assertThat(results).hasSize(ADDRESS_COUNT);
        assertThat(results.get(0)).isEqualTo(VISIT_US_ADDRESS);
        assertThat(results.get(1)).isEqualTo(NO_SECONDARY_COURT_TYPE_ADDRESS);
        assertThat(results.get(2)).isEqualTo(WRITE_TO_US_ADDRESS);

        verify(courtAddressRepository).deleteAll(any());
        verify(courtSecondaryAddressTypeRepository, atMostOnce()).deleteAllByAddressIdIn(any());
        verify(courtSecondaryAddressTypeRepository, atMostOnce()).deleteAll(any());
        verify(courtSecondaryAddressTypeRepository, atMostOnce()).saveAll(any());
        verify(adminService, never()).updateCourtLatLon(eq(COURT_SLUG), anyDouble(), anyDouble());
        verify(adminAuditService, atLeastOnce()).saveAudit(
            "Update court addresses and coordinates",
            EXPECTED_ADDRESSES,
            results,
            "court-slug"
        );
    }

    @Test
    void shouldNotUpdateCoordinatesForEmptyMapitData() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(MOCK_COURT));
        when(adminAddressTypeService.getAddressTypeMap()).thenReturn(Map.of(
            VISIT_US_ADDRESS_TYPE_ID,
            VISIT_US_ADDRESS_TYPE,
            WRITE_TO_US_ADDRESS_TYPE_ID,
            WRITE_TO_US_ADDRESS_TYPE,
            VISIT_OR_CONTACT_US_ADDRESS_TYPE_ID, VISIT_OR_CONTACT_US_ADDRESS_TYPE
        ));
        doAnswer(i -> i.getArguments()[0])
            .when(courtSecondaryAddressTypeRepository)
            .saveAll(anyList());
        when(courtAddressRepository.saveAll(any())).thenReturn(COURT_ADDRESSES_ENTITY);
        when(mapitService.getMapitData(VISIT_US_POSTCODE)).thenReturn(Optional.empty());

        final List<CourtAddress> results = adminCourtAddressService.updateCourtAddressesAndCoordinates(
            COURT_SLUG,
            EXPECTED_ADDRESSES
        );
        assertThat(results).hasSize(ADDRESS_COUNT);
        assertThat(results.get(0)).isEqualTo(VISIT_US_ADDRESS);
        assertThat(results.get(1)).isEqualTo(NO_SECONDARY_COURT_TYPE_ADDRESS);
        assertThat(results.get(2)).isEqualTo(WRITE_TO_US_ADDRESS);

        verify(courtAddressRepository).deleteAll(any());
        verify(courtSecondaryAddressTypeRepository, atMostOnce()).deleteAllByAddressIdIn(any());
        verify(courtSecondaryAddressTypeRepository, atMostOnce()).deleteAll(any());
        verify(courtSecondaryAddressTypeRepository, atMostOnce()).saveAll(any());
        verify(adminService, never()).updateCourtLatLon(eq(COURT_SLUG), anyDouble(), anyDouble());
        verify(adminAuditService, atLeastOnce()).saveAudit("Update court addresses and coordinates",
                                                           EXPECTED_ADDRESSES,
                                                           results, COURT_SLUG
        );
    }

    @Test
    void validateCourtPostcodesReturnsNothingForValidPostcodes() {
        when(adminAddressTypeService.getAddressTypeMap()).thenReturn(ADDRESS_TYPE_MAP);
        when(validationService.validateFullPostcodes(asList(VISIT_US_POSTCODE, WRITE_TO_US_POSTCODE))).thenReturn(
            emptyList());
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(MOCK_COURT));

        assertThat(adminCourtAddressService.validateCourtAddressPostcodes(EXPECTED_ADDRESSES)).isEmpty();
    }

    @Test
    void validateCourtPostcodesShouldReturnAllInvalidPostcodes() {
        final List<CourtAddress> testAddresses = asList(
            EXPECTED_ADDRESSES.get(0), // VISIT US
            EXPECTED_ADDRESSES.get(1)  // WRITE TO US
        );
        when(adminAddressTypeService.getAddressTypeMap()).thenReturn(ADDRESS_TYPE_MAP);
        when(validationService.validateFullPostcodes(asList(VISIT_US_POSTCODE, WRITE_TO_US_POSTCODE)))
            .thenReturn(asList(WRITE_TO_US_POSTCODE, VISIT_US_POSTCODE));

        assertThat(adminCourtAddressService.validateCourtAddressPostcodes(testAddresses)).containsExactly(
            WRITE_TO_US_POSTCODE, VISIT_US_POSTCODE);
    }

    @Test
    void validateCourtPostcodesShouldReturnInvalidVisitUsPostcodeOnly() {
        final List<CourtAddress> testAddresses = asList(
            EXPECTED_ADDRESSES.get(0), // VISIT US
            EXPECTED_ADDRESSES.get(1)  // WRITE TO US
        );
        when(adminAddressTypeService.getAddressTypeMap()).thenReturn(ADDRESS_TYPE_MAP);
        when(validationService.validateFullPostcodes(asList(VISIT_US_POSTCODE, WRITE_TO_US_POSTCODE)))
            .thenReturn(singletonList(VISIT_US_POSTCODE));

        assertThat(adminCourtAddressService.validateCourtAddressPostcodes(testAddresses)).containsExactly(
            VISIT_US_POSTCODE);
    }

    @Test
    void validateCourtPostcodesShouldReturnInvalidWriteToUsPostcodeOnly() {
        final List<CourtAddress> testAddresses = asList(
            EXPECTED_ADDRESSES.get(0), // VISIT US
            EXPECTED_ADDRESSES.get(1)  // WRITE TO US
        );
        when(adminAddressTypeService.getAddressTypeMap()).thenReturn(ADDRESS_TYPE_MAP);
        when(validationService.validateFullPostcodes(asList(VISIT_US_POSTCODE, WRITE_TO_US_POSTCODE)))
            .thenReturn(singletonList(WRITE_TO_US_POSTCODE));

        assertThat(adminCourtAddressService.validateCourtAddressPostcodes(testAddresses)).containsExactly(
            WRITE_TO_US_POSTCODE);
    }

    @Test
    void validateCourtPostcodesShouldReturnInvalidPartialPostcode() {
        final List<CourtAddress> testAddresses = singletonList(
            new CourtAddress(1, WRITE_TO_US_ADDRESS_TYPE_ID, TEST_ADDRESS1, TEST_ADDRESS_CY1, TEST_TOWN1,
                             null, COUNTY_ID, PARTIAL_POSTCODE, COURT_SECONDARY_ADDRESS_TYPE_LIST
            )
        );
        when(adminAddressTypeService.getAddressTypeMap()).thenReturn(ADDRESS_TYPE_MAP);
        when(validationService.validateFullPostcodes(singletonList(PARTIAL_POSTCODE)))
            .thenReturn(singletonList(PARTIAL_POSTCODE));

        assertThat(adminCourtAddressService.validateCourtAddressPostcodes(testAddresses)).containsExactly(
            PARTIAL_POSTCODE);
    }

    @Test
    void validateCourtPostcodesShouldReturnNothingForEmptyAddress() {
        assertThat(adminCourtAddressService.validateCourtAddressPostcodes(emptyList())).isEmpty();
        verifyNoInteractions(validationService);
    }

    @Test
    void validateCourtPostcodesShouldNotUpdateCoordinatesForAddressWithMissingPostcode() {
        final List<CourtAddress> testAddresses =
            singletonList(new CourtAddress(1, VISIT_OR_CONTACT_US_ADDRESS_TYPE_ID, TEST_ADDRESS1, TEST_ADDRESS_CY1,
                                           TEST_TOWN1, null, COUNTY_ID, "", COURT_SECONDARY_ADDRESS_TYPE_LIST
            ));
        when(adminAddressTypeService.getAddressTypeMap()).thenReturn(ADDRESS_TYPE_MAP);
        assertThat(adminCourtAddressService.validateCourtAddressPostcodes(testAddresses)).isEmpty();
        verifyNoInteractions(validationService);
    }
}
