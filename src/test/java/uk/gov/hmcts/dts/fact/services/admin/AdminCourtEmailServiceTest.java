package uk.gov.hmcts.dts.fact.services.admin;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtEmail;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.Email;
import uk.gov.hmcts.dts.fact.model.admin.EmailType;
import uk.gov.hmcts.dts.fact.repositories.CourtEmailRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.EmailTypeRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyIterable;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtEmailService.class)
class AdminCourtEmailServiceTest {
    private static final String COURT_SLUG = "some slug";

    private static final int TEST_EMAIL_TYPE = 1;
    private static final int TEST_EMAIL_TYPE2 = 2;
    private static final int TEST_EMAIL_TYPE3 = 3;

    private static final String TEST_EMAIL_ADDRESS = "fancy.pancy.email@cat.com";
    private static final String TEST_EMAIL_ADDRESS2 = "fancy.pancy.email@cat2.com";
    private static final String TEST_EMAIL_ADDRESS3 = "fancy.pancy.email@cat3.com";

    private static final String TEST_EMAIL_EXPLANATION = "explanation";
    private static final String TEST_EMAIL_EXPLANATION2 = "explanation2";
    private static final String TEST_EMAIL_EXPLANATION3 = "explanation3";

    private static final String TEST_EMAIL_DESCRIPTION = "Enquiries";
    private static final String TEST_EMAIL_DESCRIPTION2 = "Finances";
    private static final String TEST_EMAIL_DESCRIPTION3 = "Crown court";

    private static final String TEST_EMAIL_DESCRIPTION_CY = "description cy";
    private static final String TEST_EMAIL_DESCRIPTION_CY2 = "description cy2";
    private static final String TEST_EMAIL_DESCRIPTION_CY3 = "description cy3";

    private static final String TEST_EMAIL_EXPLANATION_CY = "explanation cy";
    private static final String TEST_EMAIL_EXPLANATION_CY2 = "explanation cy2";
    private static final String TEST_EMAIL_EXPLANATION_CY3 = "explanation cy3";

    private static final String NOT_FOUND = "Not found: ";

    private static final uk.gov.hmcts.dts.fact.entity.EmailType EMAIL_TYPE1 =
        new uk.gov.hmcts.dts.fact.entity.EmailType(TEST_EMAIL_TYPE, TEST_EMAIL_DESCRIPTION, TEST_EMAIL_DESCRIPTION_CY);
    private static final uk.gov.hmcts.dts.fact.entity.EmailType EMAIL_TYPE2 =
        new uk.gov.hmcts.dts.fact.entity.EmailType(TEST_EMAIL_TYPE2, TEST_EMAIL_DESCRIPTION2, TEST_EMAIL_DESCRIPTION_CY2);
    private static final uk.gov.hmcts.dts.fact.entity.EmailType EMAIL_TYPE3 =
        new uk.gov.hmcts.dts.fact.entity.EmailType(TEST_EMAIL_TYPE3, TEST_EMAIL_DESCRIPTION3, TEST_EMAIL_DESCRIPTION_CY3);
    private static final List<uk.gov.hmcts.dts.fact.entity.EmailType> EMAIL_TYPES =
        Arrays.asList(EMAIL_TYPE1, EMAIL_TYPE2, EMAIL_TYPE3);

    private static final int EMAIL_COUNT = 3;

    private static final List<CourtEmail> COURT_EMAILS = new ArrayList<>();

    private static final List<Email> EXPECTED_EMAILS = Arrays.asList(
        new Email(TEST_EMAIL_ADDRESS, TEST_EMAIL_EXPLANATION, TEST_EMAIL_EXPLANATION_CY, TEST_EMAIL_TYPE),
        new Email(TEST_EMAIL_ADDRESS2, TEST_EMAIL_EXPLANATION2, TEST_EMAIL_EXPLANATION_CY2, TEST_EMAIL_TYPE2),
        new Email(TEST_EMAIL_ADDRESS3, TEST_EMAIL_EXPLANATION3, TEST_EMAIL_EXPLANATION_CY3, TEST_EMAIL_TYPE3)
    );

    @Autowired
    private AdminCourtEmailService adminService;

    @MockitoBean
    private CourtRepository courtRepository;

    @MockitoBean
    private CourtEmailRepository emailRepository;

    @MockitoBean
    private EmailTypeRepository emailTypeRepository;

    @MockitoBean
    private AdminAuditService adminAuditService;

    @Mock
    private Court court;

    @BeforeAll
    static void setUp() {

        for (int i = 0; i < EMAIL_COUNT; i++) {
            CourtEmail courtEmail = mock(CourtEmail.class);
            COURT_EMAILS.add(courtEmail);
        }

        when(COURT_EMAILS.get(0).getEmail())
            .thenReturn(new uk.gov.hmcts.dts.fact.entity.Email(
                TEST_EMAIL_ADDRESS, TEST_EMAIL_EXPLANATION, TEST_EMAIL_EXPLANATION_CY, EMAIL_TYPE1));

        when(COURT_EMAILS.get(1).getEmail())
            .thenReturn(new uk.gov.hmcts.dts.fact.entity.Email(
                TEST_EMAIL_ADDRESS2, TEST_EMAIL_EXPLANATION2, TEST_EMAIL_EXPLANATION_CY2, EMAIL_TYPE2));

        when(COURT_EMAILS.get(2).getEmail())
            .thenReturn(new uk.gov.hmcts.dts.fact.entity.Email(
                TEST_EMAIL_ADDRESS3, TEST_EMAIL_EXPLANATION3, TEST_EMAIL_EXPLANATION_CY3, EMAIL_TYPE3));
    }

    @Test
    void shouldReturnAllEmails() {
        when(court.getCourtEmails()).thenReturn(COURT_EMAILS);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));

        assertThat(adminService.getCourtEmailsBySlug(COURT_SLUG))
            .hasSize(EMAIL_COUNT)
            .first()
            .isInstanceOf(Email.class);
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingEmailsForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getCourtEmailsBySlug(COURT_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }

    @Test
    void shouldUpdateCourtEmails() {

        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(court.getCourtEmails()).thenReturn(COURT_EMAILS);
        when(emailRepository.saveAll(any())).thenReturn(COURT_EMAILS);

        List<Email> emails = adminService.updateEmailListForCourt(COURT_SLUG, EXPECTED_EMAILS);
        verify(emailRepository).deleteAll(COURT_EMAILS);
        verify(emailRepository).saveAll(anyIterable());

        assertThat(emails)
            .hasSize(EMAIL_COUNT)
            .containsExactlyElementsOf(EXPECTED_EMAILS);
        verify(adminAuditService, atLeastOnce()).saveAudit("Update court email list",
                                                           EXPECTED_EMAILS,
                                                           emails, COURT_SLUG);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingEmailsForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.updateEmailListForCourt(COURT_SLUG, any()))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldReturnAllEmailTypesInAlphabeticalOrder() {
        when(emailTypeRepository.findAll()).thenReturn(EMAIL_TYPES);

        final List<EmailType> results = adminService.getAllEmailTypes();
        assertThat(results).hasSize(EMAIL_TYPES.size());
        assertThat(results.get(0).getId()).isEqualTo(TEST_EMAIL_TYPE3);
        assertThat(results.get(0).getDescription()).isEqualTo(TEST_EMAIL_DESCRIPTION3);
        assertThat(results.get(0).getDescriptionCy()).isEqualTo(TEST_EMAIL_DESCRIPTION_CY3);
        assertThat(results.get(1).getId()).isEqualTo(TEST_EMAIL_TYPE);
        assertThat(results.get(1).getDescription()).isEqualTo(TEST_EMAIL_DESCRIPTION);
        assertThat(results.get(1).getDescriptionCy()).isEqualTo(TEST_EMAIL_DESCRIPTION_CY);
        assertThat(results.get(2).getId()).isEqualTo(TEST_EMAIL_TYPE2);
        assertThat(results.get(2).getDescription()).isEqualTo(TEST_EMAIL_DESCRIPTION2);
        assertThat(results.get(2).getDescriptionCy()).isEqualTo(TEST_EMAIL_DESCRIPTION_CY2);
    }
}
