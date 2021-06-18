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
import uk.gov.hmcts.dts.fact.repositories.CourtPostcodeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtPostcodeService.class)
public class AdminCourtPostcodeServiceTest {
    private static final String COURT_SLUG = "test-slug";
    private static final String NOT_FOUND = "Not found: ";
    private static final List<String> POSTCODES = Arrays.asList(
        "M1",
        "M2",
        "M3",
        "M47ER"
    );
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
        new CourtPostcode(POSTCODES.get(0), court),
        new CourtPostcode(POSTCODES.get(1), court),
        new CourtPostcode(POSTCODES.get(2), court),
        new CourtPostcode(POSTCODES.get(3), court)
    );

    @Test
    void shouldReturnAllCourtPostcodes() {
        when(court.getCourtPostcodes()).thenReturn(courtPostcodes);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));

        List<String> results = adminService.getCourtPostcodesBySlug(COURT_SLUG);
        assertThat(results)
            .hasSize(POSTCODE_COUNT);
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingPostcodesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getCourtPostcodesBySlug(COURT_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }

//    @Test
//    void shouldAddCourtPostcodes() {
//        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
//        when(court.getCourtPostcodes()).thenReturn(courtPostcodes);
//        for (int i = 0; i < courtPostcodes.size(); i++) {
//            when(courtPostcodeRepository.save(courtPostcodes.get(i))).thenReturn(courtPostcodes.get(i));
//        }
//        adminService.addCourtPostcodes(COURT_SLUG, POSTCODES);
//        verify(courtPostcodeRepository, times(POSTCODE_COUNT)).save(any());
//    }
//
//    @Test
//    void shouldReturnNotFoundWhenAddingPostcodesForNonExistentCourt() {
//        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> adminService.addCourtPostcodes(COURT_SLUG, POSTCODES))
//            .isInstanceOf(NotFoundException.class)
//            .hasMessage(NOT_FOUND + COURT_SLUG);
//    }
//
//    @Test
//    void shouldDeleteCourtPostcodes() {
//        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
//        when(court.getId()).thenReturn(1);
//        when(courtPostcodeRepository.findByCourtIdAndPostcode(1, POSTCODES.get(0))).thenReturn(courtPostcodes.get(0));
//        when(court.getCourtPostcodes()).thenReturn(courtPostcodes);
//
//        for (int i = 0; i < courtPostcodes.size(); i++) {
//            when(courtPostcodeRepository.save(courtPostcodes.get(i))).thenReturn(courtPostcodes.get(i));
//        }
//
//        adminService.deleteCourtPostcodes(COURT_SLUG, POSTCODES.get(0));
//        verify(courtPostcodeRepository, times(POSTCODE_COUNT)).save(any());
//    }
}
