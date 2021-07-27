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
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtContact;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.Contact;
import uk.gov.hmcts.dts.fact.model.admin.CourtAddress;
import uk.gov.hmcts.dts.fact.repositories.ContactTypeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtAddressRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtContactRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminAddressTypeService;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtAddressService.class)
public class AdminCourtAddressServiceTest {
    private static final String COURT_SLUG = "some slug";
    private static final int TEST_TYPE_ID1 = 1;
    private static final int TEST_TYPE_ID2 = 2;
    private static final String TEST_TYPE1 = "some type";
    private static final String TEST_TYPE2 = "another type";
    private static final String TEST_TYPE_CY1 = TEST_TYPE1 + " cy";
    private static final String TEST_TYPE_CY2 = TEST_TYPE2 + " cy";

    private static final List<String> TEST_ADDRESS1 = Arrays.asList(
        "1 High Street"
    );
    private static final List<String> TEST_ADDRESS2 = Arrays.asList(
        "High Court",
        "2 Main Road"
    );
    private static final List<String> TEST_ADDRESS3 = Arrays.asList(
        "PO Box 123"
    );
    private static final String TEST_TOWN1 = "London";
    private static final String TEST_TOWN2 = "Manchester";
    private static final String TEST_TOWN3 = "Birmingham";
    private static final String TEST_POSTCODE1 = "EC1A 1AA";
    private static final String TEST_POSTCODE2 = "M1 2AA";
    private static final String TEST_POSTCODE3 = "B1 3AA";

    private static final String NOT_FOUND = "Not found: ";

    private static final int ADDRESS_COUNT = 3;
    private static final List<CourtAddress> EXPECTED_ADDRESSES = Arrays.asList(
        new CourtAddress(TEST_TYPE_ID1, TEST_ADDRESS1, null, TEST_TOWN1, null, TEST_POSTCODE1),
        new CourtAddress(TEST_TYPE_ID2, TEST_ADDRESS2, null, TEST_TOWN2, null, TEST_POSTCODE2),
        new CourtAddress(TEST_TYPE_ID2, TEST_ADDRESS3, null, TEST_TOWN3, null, TEST_POSTCODE3)
    );

    private static final uk.gov.hmcts.dts.fact.entity.AddressType ADDRESS_TYPE1 = new uk.gov.hmcts.dts.fact.entity.AddressType(TEST_TYPE_ID1, TEST_TYPE1, TEST_TYPE_CY1);
    private static final uk.gov.hmcts.dts.fact.entity.AddressType ADDRESS_TYPE2 = new uk.gov.hmcts.dts.fact.entity.AddressType(TEST_TYPE_ID2, TEST_TYPE2, TEST_TYPE_CY2);
    private static final Court MOCK_COURT = mock(Court.class);

    private static final List<uk.gov.hmcts.dts.fact.entity.CourtAddress> COURT_ADDRESSES_ENTITY = Arrays.asList(
        new uk.gov.hmcts.dts.fact.entity.CourtAddress(MOCK_COURT, ADDRESS_TYPE1, TEST_ADDRESS1, null, TEST_TOWN1, null, TEST_POSTCODE1),
        new uk.gov.hmcts.dts.fact.entity.CourtAddress(MOCK_COURT, ADDRESS_TYPE2, TEST_ADDRESS2, null, TEST_TOWN2, null, TEST_POSTCODE2),
        new uk.gov.hmcts.dts.fact.entity.CourtAddress(MOCK_COURT, ADDRESS_TYPE2, TEST_ADDRESS3, null, TEST_TOWN3, null, TEST_POSTCODE3)
    );

    @Autowired
    private AdminCourtAddressService adminCourtAddressService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private CourtAddressRepository courtAddressRepository;

    @MockBean
    private AdminAddressTypeService adminAddressTypeService;

    @Test
    void shouldReturnAllCourtAddresses() {
        when(MOCK_COURT.getAddresses()).thenReturn(COURT_ADDRESSES_ENTITY);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(MOCK_COURT));

        List<CourtAddress> results = adminCourtAddressService.getCourtAddressesBySlug(COURT_SLUG);
        assertThat(results)
            .hasSize(ADDRESS_COUNT)
            .first()
            .isInstanceOf(CourtAddress.class);
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingAddressesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtAddressService.getCourtAddressesBySlug(COURT_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }

//    @Test
//    void shouldUpdateCourtAddresses() {
//        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(MOCK_COURT));
//        when(adminAddressTypeService.getAddressTypeMap()).thenReturn(Map.of(TEST_TYPE_ID1, ADDRESS_TYPE1, TEST_TYPE_ID2, ADDRESS_TYPE2));
//        when(courtAddressRepository.saveAll(any())).thenReturn(COURT_ADDRESSES_ENTITY);
//
//        assertThat(adminCourtAddressService.updateCourtAddresses(COURT_SLUG, EXPECTED_ADDRESSES))
//            .hasSize(ADDRESS_COUNT)
//            .containsExactlyElementsOf(EXPECTED_ADDRESSES);
//
//        verify(courtAddressRepository).deleteAll(any());
//        verify(courtAddressRepository).saveAll(any());
//    }

    @Test
    void shouldReturnNotFoundWhenUpdatingAddressesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtAddressService.updateCourtAddresses(COURT_SLUG, any()))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }
}
