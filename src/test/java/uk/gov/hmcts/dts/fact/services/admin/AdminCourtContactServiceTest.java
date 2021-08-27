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
import uk.gov.hmcts.dts.fact.model.admin.ContactType;
import uk.gov.hmcts.dts.fact.repositories.ContactTypeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtContactRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtContactService.class)
public class AdminCourtContactServiceTest {
    private static final String COURT_SLUG = "some slug";
    private static final int TEST_TYPE_ID1 = 1;
    private static final int TEST_TYPE_ID2 = 2;
    private static final int TEST_TYPE_ID3 = 3;
    private static final String TEST_TYPE1 = "some type";
    private static final String TEST_TYPE2 = "another type";
    private static final String TEST_TYPE3 = "yet another type";
    private static final String TEST_TYPE_CY1 = TEST_TYPE1 + " cy";
    private static final String TEST_TYPE_CY2 = TEST_TYPE2 + " cy";
    private static final String TEST_TYPE_CY3 = TEST_TYPE3 + " cy";
    private static final String TEST_NUMBER1 = "123";
    private static final String TEST_NUMBER2 = "456";
    private static final String TEST_NUMBER3 = "789";
    private static final String TEST_NUMBER4 = "000";
    private static final String TEST_DX_NUMBER = "DX 99";
    private static final String TEST_EXPLANATION1 = "some explanation";
    private static final String TEST_EXPLANATION2 = "another explanation";
    private static final String TEST_EXPLANATION3 = "yet another explanation";
    private static final String NOT_FOUND = "Not found: ";

    private static final int CONTACT_COUNT = 4;
    private static final List<CourtContact> COURT_CONTACTS = new ArrayList<>();

    private static final Contact CONTACT1 = new Contact(TEST_TYPE_ID1, TEST_NUMBER1, TEST_EXPLANATION1, TEST_EXPLANATION1, false);
    private static final Contact CONTACT2 = new Contact(TEST_TYPE_ID2, TEST_NUMBER2, TEST_EXPLANATION2, TEST_EXPLANATION2, false);
    private static final Contact CONTACT3 = new Contact(TEST_TYPE_ID3, TEST_NUMBER3, TEST_EXPLANATION3, TEST_EXPLANATION3, true);
    private static final Contact CONTACT4 = new Contact(null, TEST_NUMBER4, null, null, true);
    private static final List<Contact> EXPECTED_CONTACTS = Arrays.asList(CONTACT1, CONTACT2, CONTACT3, CONTACT4);

    private static final uk.gov.hmcts.dts.fact.entity.ContactType CONTACT_TYPE1 = new uk.gov.hmcts.dts.fact.entity.ContactType(TEST_TYPE_ID1, TEST_TYPE1, TEST_TYPE_CY1);
    private static final uk.gov.hmcts.dts.fact.entity.ContactType CONTACT_TYPE2 = new uk.gov.hmcts.dts.fact.entity.ContactType(TEST_TYPE_ID2, TEST_TYPE2, TEST_TYPE_CY2);
    private static final uk.gov.hmcts.dts.fact.entity.ContactType CONTACT_TYPE3 = new uk.gov.hmcts.dts.fact.entity.ContactType(TEST_TYPE_ID3, TEST_TYPE3, TEST_TYPE_CY3);
    private static final List<uk.gov.hmcts.dts.fact.entity.ContactType> CONTACT_TYPES = Arrays.asList(CONTACT_TYPE1, CONTACT_TYPE2, CONTACT_TYPE3);

    private static final List<uk.gov.hmcts.dts.fact.entity.Contact> CONTACT_ENTITIES = Arrays.asList(
        new uk.gov.hmcts.dts.fact.entity.Contact(CONTACT_TYPE1, TEST_NUMBER1, TEST_EXPLANATION1, TEST_EXPLANATION1, false),
        new uk.gov.hmcts.dts.fact.entity.Contact(CONTACT_TYPE2, TEST_NUMBER2, TEST_EXPLANATION2, TEST_EXPLANATION2, false),
        new uk.gov.hmcts.dts.fact.entity.Contact(CONTACT_TYPE3, TEST_NUMBER3, TEST_EXPLANATION3, TEST_EXPLANATION3, true),
        new uk.gov.hmcts.dts.fact.entity.Contact(null, TEST_NUMBER4, null, null, true)
    );

    @Autowired
    private AdminCourtContactService adminService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private CourtContactRepository courtContactRepository;

    @MockBean
    private ContactTypeRepository contactTypeRepository;

    @Mock
    private Court court;

    @BeforeAll
    static void setUp() {
        for (int i = 0; i < CONTACT_COUNT; i++) {
            final CourtContact courtContact = mock(CourtContact.class);
            when(courtContact.getContact()).thenReturn(CONTACT_ENTITIES.get(i));
            COURT_CONTACTS.add(courtContact);
        }
    }

    @Test
    void shouldReturnAllCourtContacts() {
        when(court.getCourtContacts()).thenReturn(COURT_CONTACTS);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));

        List<Contact> results = adminService.getCourtContactsBySlug(COURT_SLUG);
        assertThat(results)
            .hasSize(CONTACT_COUNT)
            .first()
            .isInstanceOf(Contact.class);
        assertThat(results.stream()).noneMatch(c -> c.getNumber().equals(TEST_DX_NUMBER));
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingContactsForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getCourtContactsBySlug(COURT_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }

    @Test
    void shouldUpdateCourtContacts() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(contactTypeRepository.findAll()).thenReturn(CONTACT_TYPES);
        when(court.getCourtContacts()).thenReturn(COURT_CONTACTS);
        when(courtContactRepository.saveAll(any())).thenReturn(COURT_CONTACTS);

        assertThat(adminService.updateCourtContacts(COURT_SLUG, EXPECTED_CONTACTS))
            .hasSize(CONTACT_COUNT)
            .containsExactlyElementsOf(EXPECTED_CONTACTS);

        verify(courtContactRepository).deleteAll(COURT_CONTACTS);
        verify(courtContactRepository).saveAll(any());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingContactsForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.updateCourtContacts(COURT_SLUG, any()))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }

    @Test
    void shouldReturnAllContactTypesInAlphabeticalOrder() {
        when(contactTypeRepository.findAll()).thenReturn(CONTACT_TYPES);

        final List<ContactType> results = adminService.getAllCourtContactTypes();
        assertThat(results).hasSize(CONTACT_TYPES.size());

        assertThat(results.get(0).getId()).isEqualTo(TEST_TYPE_ID2);
        assertThat(results.get(0).getType()).isEqualTo(TEST_TYPE2);
        assertThat(results.get(0).getTypeCy()).isEqualTo(TEST_TYPE_CY2);
        assertThat(results.get(1).getId()).isEqualTo(TEST_TYPE_ID1);
        assertThat(results.get(1).getType()).isEqualTo(TEST_TYPE1);
        assertThat(results.get(1).getTypeCy()).isEqualTo(TEST_TYPE_CY1);
        assertThat(results.get(2).getId()).isEqualTo(TEST_TYPE_ID3);
        assertThat(results.get(2).getType()).isEqualTo(TEST_TYPE3);
        assertThat(results.get(2).getTypeCy()).isEqualTo(TEST_TYPE_CY3);
    }
}
