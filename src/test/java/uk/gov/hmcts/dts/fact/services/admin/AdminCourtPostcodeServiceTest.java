package uk.gov.hmcts.dts.fact.services.admin;

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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtPostcodeService.class)
public class AdminCourtPostcodeServiceTest {
    private static final String COURT_SLUG = "test-slug";
    private static final String NOT_FOUND = "Not found: ";

    private static final int TEST_COURT_ID = 1;
    private static final String TEST_POSTCODE1 = "M1";
    private static final String TEST_POSTCODE2 = "M2";
    private static final String TEST_POSTCODE3 = "M3";
    private static final String TEST_POSTCODE4 = "M47ER";
    private static final String NEW_POSTCODE = "M5";

    private static final List<String> POSTCODES = Arrays.asList(
        TEST_POSTCODE1,
        TEST_POSTCODE2,
        TEST_POSTCODE3,
        TEST_POSTCODE4
    );
    private static final List<String> POSTCODES_TO_BE_DELETED = Arrays.asList(
        TEST_POSTCODE2,
        TEST_POSTCODE3
    );
    private static final List<String> POSTCODES_TO_BE_ADDED = Collections.singletonList(NEW_POSTCODE);
    private static final int POSTCODE_COUNT = POSTCODES.size();

    @Autowired
    private AdminCourtPostcodeService adminService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private CourtPostcodeRepository courtPostcodeRepository;

    @Mock
    private Court court;

    private final List<CourtPostcode> courtPostcodes = Arrays.asList(
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

        assertThat(adminService.addCourtPostcodes(COURT_SLUG, POSTCODES_TO_BE_ADDED))
            .hasSize(1)
            .containsExactly(NEW_POSTCODE);
    }

    @Test
    void shouldReturnNotFoundWhenAddingPostcodesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.addCourtPostcodes(COURT_SLUG, POSTCODES))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);

        verifyNoInteractions(courtPostcodeRepository);
    }

    @Test
    void shouldDeleteCourtPostcodes() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(court.getId()).thenReturn(TEST_COURT_ID);
        when(courtPostcodeRepository.deleteByCourtIdAndPostcode(TEST_COURT_ID, TEST_POSTCODE2)).thenReturn(Collections.singletonList(courtPostcodes.get(1)));
        when(courtPostcodeRepository.deleteByCourtIdAndPostcode(TEST_COURT_ID, TEST_POSTCODE3)).thenReturn(Collections.singletonList(courtPostcodes.get(2)));

        assertThat(adminService.deleteCourtPostcodes(COURT_SLUG, POSTCODES_TO_BE_DELETED)).isEqualTo(2);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingPostcodesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.deleteCourtPostcodes(COURT_SLUG, POSTCODES_TO_BE_DELETED))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);

        verifyNoInteractions(courtPostcodeRepository);
    }

    @Test
    void shouldCheckPostcodesExistForCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtPostcodeRepository.findByCourtIdAndPostcode(court.getId(), TEST_POSTCODE2)).thenReturn(Collections.singletonList(courtPostcodes.get(1)));
        when(courtPostcodeRepository.findByCourtIdAndPostcode(court.getId(), TEST_POSTCODE3)).thenReturn(Collections.singletonList(courtPostcodes.get(2)));

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
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtPostcodeRepository.findByCourtIdAndPostcode(court.getId(), TEST_POSTCODE2)).thenReturn(Collections.singletonList(courtPostcodes.get(1)));
        when(courtPostcodeRepository.findByCourtIdAndPostcode(court.getId(), TEST_POSTCODE3)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> adminService.checkPostcodesExist(COURT_SLUG, POSTCODES_TO_BE_DELETED))
            .isInstanceOf(PostcodeNotFoundException.class)
            .hasMessage("Postcodes do not exist: [" + TEST_POSTCODE3 + "]");
    }

    @Test
    void shouldCheckPostcodesDoNotExistForCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtPostcodeRepository.findByCourtIdAndPostcode(court.getId(), TEST_POSTCODE2)).thenReturn(Collections.emptyList());
        when(courtPostcodeRepository.findByCourtIdAndPostcode(court.getId(), TEST_POSTCODE3)).thenReturn(Collections.emptyList());

        assertThatCode(() -> adminService.checkPostcodesDoNotExist(COURT_SLUG, POSTCODES_TO_BE_DELETED))
            .doesNotThrowAnyException();
    }

    @Test
    void shouldReturnNotFoundWhenCheckingPostcodesDoNotExistForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.checkPostcodesDoNotExist(COURT_SLUG, POSTCODES_TO_BE_DELETED))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);

        verifyNoInteractions(courtPostcodeRepository);
    }

    @Test
    void shouldReturnPostcodesExistedWhenTheyExist() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtPostcodeRepository.findByCourtIdAndPostcode(court.getId(), TEST_POSTCODE2)).thenReturn(Collections.singletonList(courtPostcodes.get(1)));
        when(courtPostcodeRepository.findByCourtIdAndPostcode(court.getId(), TEST_POSTCODE3)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> adminService.checkPostcodesDoNotExist(COURT_SLUG, POSTCODES_TO_BE_DELETED))
            .isInstanceOf(PostcodeExistedException.class)
            .hasMessage("Postcodes already exist: [" + TEST_POSTCODE2 + "]");
    }
}
