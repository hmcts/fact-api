package uk.gov.hmcts.dts.fact.services.admin;

import com.launchdarkly.shaded.com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtPostcode;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.exception.PostcodeExistedException;
import uk.gov.hmcts.dts.fact.exception.PostcodeNotFoundException;
import uk.gov.hmcts.dts.fact.repositories.CourtPostcodeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtPostcodeService.class)
@SuppressWarnings("PMD.TooManyMethods")
class AdminCourtPostcodeServiceTest {
    private static final String COURT_SLUG = "test-slug";
    private static final String SOURCE_COURT_SLUG = "source-slug";
    private static final String DESTINATION_COURT_SLUG = "destination-slug";
    private static final String NOT_FOUND = "Not found: ";

    private static final int TEST_COURT_ID = 1;
    private static final String TEST_POSTCODE1 = "M1";
    private static final String TEST_POSTCODE2 = "M2";
    private static final String TEST_POSTCODE3 = "M3";
    private static final String TEST_POSTCODE4 = "M47ER";
    private static final String NEW_POSTCODE = "M5";

    private static final List<String> POSTCODES = asList(
        TEST_POSTCODE1,
        TEST_POSTCODE2,
        TEST_POSTCODE3,
        TEST_POSTCODE4
    );
    private static final List<String> POSTCODES_TO_BE_DELETED = asList(
        TEST_POSTCODE2,
        TEST_POSTCODE3
    );
    private static final List<String> POSTCODES_TO_BE_ADDED = POSTCODES_TO_BE_DELETED;
    private static final List<String> POSTCODES_TO_BE_MOVED = POSTCODES_TO_BE_DELETED;
    private static final int POSTCODE_COUNT = POSTCODES.size();

    @Autowired
    private AdminCourtPostcodeService adminService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private CourtPostcodeRepository courtPostcodeRepository;

    @MockBean
    private AdminAuditService adminAuditService;

    @Mock
    private Court court;

    private final List<CourtPostcode> courtPostcodes = asList(
        new CourtPostcode(TEST_POSTCODE1, court),
        new CourtPostcode(TEST_POSTCODE2, court),
        new CourtPostcode(TEST_POSTCODE3, court),
        new CourtPostcode(TEST_POSTCODE4, court)
    );

    @Test
    void shouldReturnAllCourtPostcodes() {
        when(court.getCourtPostcodes()).thenReturn(courtPostcodes);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));

        assertThat(adminService.getCourtPostcodesBySlug(COURT_SLUG))
            .hasSize(POSTCODE_COUNT)
            .containsExactlyElementsOf(POSTCODES);
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingPostcodesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getCourtPostcodesBySlug(COURT_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);

        verifyNoInteractions(courtPostcodeRepository);
    }

    @Test
    void shouldAddCourtPostcodes() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtPostcodeRepository.save(any())).thenReturn(new CourtPostcode(NEW_POSTCODE, court));

        List<String> results = adminService.addCourtPostcodes(COURT_SLUG, singletonList(NEW_POSTCODE));
        assertThat(results)
            .hasSize(1)
            .containsExactly(NEW_POSTCODE);
        verify(adminAuditService, atLeastOnce()).saveAudit("Create court postcodes",
                                                           emptyList(),
                                                           results, COURT_SLUG);
    }

    @Test
    void shouldReturnNotFoundWhenAddingPostcodesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.addCourtPostcodes(COURT_SLUG, POSTCODES))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);

        verifyNoInteractions(courtPostcodeRepository);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldDeleteCourtPostcodes() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(court.getCourtPostcodes()).thenReturn(courtPostcodes).thenReturn(emptyList());
        when(court.getId()).thenReturn(TEST_COURT_ID);
        when(courtPostcodeRepository.deleteByCourtIdAndPostcode(TEST_COURT_ID, TEST_POSTCODE2)).thenReturn(singletonList(courtPostcodes.get(1)));
        when(courtPostcodeRepository.deleteByCourtIdAndPostcode(TEST_COURT_ID, TEST_POSTCODE3)).thenReturn(singletonList(courtPostcodes.get(2)));

        int rowsDeleted = adminService.deleteCourtPostcodes(COURT_SLUG, POSTCODES_TO_BE_DELETED);
        assertThat(rowsDeleted).isEqualTo(2);
        verify(adminAuditService, atLeastOnce()).saveAudit("Delete court postcodes",
                                                           courtPostcodes.stream()
                                                               .map(CourtPostcode::getPostcode)
                                                               .collect(toList()),
                                                           emptyList(),
                                                           null);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingPostcodesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.deleteCourtPostcodes(COURT_SLUG, POSTCODES_TO_BE_DELETED))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);

        verifyNoInteractions(courtPostcodeRepository);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldMoveCourtPostcodes() {
        final Court sourceCourt = mock(Court.class);
        when(sourceCourt.getId()).thenReturn(1);
        when(courtRepository.findBySlug(SOURCE_COURT_SLUG)).thenReturn(Optional.of(sourceCourt));
        final List<CourtPostcode> courtPostcodes = asList(
            new CourtPostcode(TEST_POSTCODE2, sourceCourt),
            new CourtPostcode(TEST_POSTCODE3, sourceCourt)
        );
        when(courtPostcodeRepository.findByCourtIdAndPostcodeIn(1, POSTCODES_TO_BE_MOVED)).thenReturn(courtPostcodes);

        final Court destinationCourt = mock(Court.class);
        when(destinationCourt.getId()).thenReturn(2);
        when(courtRepository.findBySlug(DESTINATION_COURT_SLUG)).thenReturn(Optional.of(destinationCourt));
        when(courtPostcodeRepository.findByCourtIdAndPostcodeIn(2, POSTCODES_TO_BE_MOVED)).thenReturn(emptyList());

        courtPostcodes.get(0).setCourt(destinationCourt);
        courtPostcodes.get(1).setCourt(destinationCourt);
        when(courtPostcodeRepository.saveAll(courtPostcodes)).thenReturn(courtPostcodes);

        List<String> results = adminService.moveCourtPostcodes(SOURCE_COURT_SLUG, DESTINATION_COURT_SLUG, POSTCODES_TO_BE_MOVED);
        assertThat(results)
            .hasSize(2)
            .containsExactlyInAnyOrderElementsOf(POSTCODES_TO_BE_MOVED);
        JsonObject auditData = new JsonObject();
        auditData.addProperty("moved-from", SOURCE_COURT_SLUG);
        auditData.addProperty("moved-to", DESTINATION_COURT_SLUG);
        auditData.addProperty("postcodes", results.toString());

        verify(adminAuditService, atLeastOnce()).saveAudit("Move court postcodes",
                                                           auditData,
                                                           POSTCODES_TO_BE_DELETED,
                                                           SOURCE_COURT_SLUG);
    }

    @Test
    void shouldReturnErrorWhenPostcodesNotInSourceCourt() {
        final Court sourceCourt = mock(Court.class);
        when(sourceCourt.getId()).thenReturn(1);
        when(courtRepository.findBySlug(SOURCE_COURT_SLUG)).thenReturn(Optional.of(sourceCourt));
        when(courtPostcodeRepository.findByCourtIdAndPostcodeIn(1, POSTCODES_TO_BE_MOVED)).thenReturn(emptyList());

        assertThatThrownBy(() -> adminService.moveCourtPostcodes(SOURCE_COURT_SLUG, DESTINATION_COURT_SLUG, POSTCODES_TO_BE_MOVED))
            .isInstanceOf(PostcodeNotFoundException.class)
            .hasMessage("Postcodes do not exist: " + POSTCODES_TO_BE_MOVED);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldReturnErrorWhenPostcodesExistedInDestinationCourt() {
        final Court sourceCourt = mock(Court.class);
        when(sourceCourt.getId()).thenReturn(1);
        when(courtRepository.findBySlug(SOURCE_COURT_SLUG)).thenReturn(Optional.of(sourceCourt));
        final List<CourtPostcode> courtPostcodes = asList(
            new CourtPostcode(TEST_POSTCODE2, sourceCourt),
            new CourtPostcode(TEST_POSTCODE3, sourceCourt)
        );
        when(courtPostcodeRepository.findByCourtIdAndPostcodeIn(1, POSTCODES_TO_BE_MOVED)).thenReturn(courtPostcodes);

        final Court destinationCourt = mock(Court.class);
        when(destinationCourt.getId()).thenReturn(2);
        when(courtRepository.findBySlug(DESTINATION_COURT_SLUG)).thenReturn(Optional.of(destinationCourt));
        when(courtPostcodeRepository.findByCourtIdAndPostcodeIn(2, POSTCODES_TO_BE_MOVED)).thenReturn(courtPostcodes);

        assertThatThrownBy(() -> adminService.moveCourtPostcodes(SOURCE_COURT_SLUG, DESTINATION_COURT_SLUG, POSTCODES_TO_BE_MOVED))
            .isInstanceOf(PostcodeExistedException.class)
            .hasMessage("Postcodes already exist: " + POSTCODES_TO_BE_MOVED);

        verify(courtPostcodeRepository, never()).saveAll(any());
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldReturnNotFoundWhenMovingPostcodesForNonExistentSourceCourt() {
        when(courtRepository.findBySlug(SOURCE_COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.moveCourtPostcodes(SOURCE_COURT_SLUG, DESTINATION_COURT_SLUG, POSTCODES_TO_BE_MOVED))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + SOURCE_COURT_SLUG);

        verifyNoInteractions(courtPostcodeRepository);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldReturnNotFoundWhenMovingPostcodesForNonExistentDestinationCourt() {
        when(courtRepository.findBySlug(SOURCE_COURT_SLUG)).thenReturn(Optional.of(court));
        final List<CourtPostcode> courtPostcodesInSource = asList(
            new CourtPostcode(TEST_POSTCODE2, court),
            new CourtPostcode(TEST_POSTCODE3, court),
            new CourtPostcode(TEST_POSTCODE3, court)
        );
        when(courtPostcodeRepository.findByCourtIdAndPostcodeIn(court.getId(), POSTCODES_TO_BE_MOVED)).thenReturn(courtPostcodesInSource);
        when(courtRepository.findBySlug(DESTINATION_COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.moveCourtPostcodes(SOURCE_COURT_SLUG, DESTINATION_COURT_SLUG, POSTCODES_TO_BE_MOVED))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + DESTINATION_COURT_SLUG);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldCheckPostcodesExistForCourt() {
        final List<CourtPostcode> courtPostcodes = asList(
            new CourtPostcode(TEST_POSTCODE2, court),
            new CourtPostcode(TEST_POSTCODE3, court));

        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtPostcodeRepository.findByCourtIdAndPostcodeIn(court.getId(), POSTCODES_TO_BE_DELETED)).thenReturn(courtPostcodes);

        assertThatCode(() -> adminService.checkPostcodesExist(COURT_SLUG, POSTCODES_TO_BE_DELETED))
            .doesNotThrowAnyException();
    }

    @Test
    void shouldReturnNotFoundWhenCheckingPostcodesExistForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.checkPostcodesExist(COURT_SLUG, POSTCODES_TO_BE_DELETED))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);

        verifyNoInteractions(courtPostcodeRepository);
    }

    @Test
    void shouldReturnPostcodesNotFoundWhenTheyDoNotExist() {
        final List<CourtPostcode> courtPostcodesFound = singletonList(new CourtPostcode(TEST_POSTCODE2, court));
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtPostcodeRepository.findByCourtIdAndPostcodeIn(court.getId(), POSTCODES_TO_BE_DELETED)).thenReturn(courtPostcodesFound);

        assertThatThrownBy(() -> adminService.checkPostcodesExist(COURT_SLUG, POSTCODES_TO_BE_DELETED))
            .isInstanceOf(PostcodeNotFoundException.class)
            .hasMessage("Postcodes do not exist: [" + TEST_POSTCODE3 + "]");
    }

    @Test
    void shouldCheckPostcodesDoNotExistForCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtPostcodeRepository.findByCourtIdAndPostcodeIn(court.getId(), POSTCODES_TO_BE_ADDED)).thenReturn(emptyList());

        assertThatCode(() -> adminService.checkPostcodesDoNotExist(COURT_SLUG, POSTCODES_TO_BE_ADDED))
            .doesNotThrowAnyException();
    }

    @Test
    void shouldReturnNotFoundWhenCheckingPostcodesDoNotExistForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.checkPostcodesDoNotExist(COURT_SLUG, POSTCODES_TO_BE_ADDED))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);

        verifyNoInteractions(courtPostcodeRepository);
    }

    @Test
    void shouldReturnPostcodesExistedWhenTheyExist() {
        final List<CourtPostcode> duplicatedPostcodes = singletonList(new CourtPostcode(TEST_POSTCODE2, court));
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtPostcodeRepository.findByCourtIdAndPostcodeIn(court.getId(), POSTCODES_TO_BE_ADDED)).thenReturn(duplicatedPostcodes);

        assertThatThrownBy(() -> adminService.checkPostcodesDoNotExist(COURT_SLUG, POSTCODES_TO_BE_ADDED))
            .isInstanceOf(PostcodeExistedException.class)
            .hasMessage("Postcodes already exist: [" + TEST_POSTCODE2 + "]");
    }
}
