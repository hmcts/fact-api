package uk.gov.hmcts.dts.fact.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.dts.fact.model.CourtReference;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
public class CourtsServiceTest {
    private static final String EXPECTED_COURT_NAME = "Sheffield Magistrates' Court";

    @Autowired
    CourtService courtService;

    @Test
    void shouldFindCourtWithMissingApostropheAndIncorrectSpacingInCourtName() {
        final List<CourtReference> results = courtService.getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch("SHEFFIELDmagistrates   court");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo(EXPECTED_COURT_NAME);
    }

    @Test
    void shouldFindCourtWithMissingWordInCourtName() {
        final List<CourtReference> results = courtService.getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch("Sheffield court");
        assertThat(results).hasSizeGreaterThanOrEqualTo(1);
        assertThat(results.stream().anyMatch(r -> r.getName().equals(EXPECTED_COURT_NAME))).isTrue();
    }

    @Test
    void shouldFindCourtWithMisspeltCourtName() {
        final List<CourtReference> results = courtService.getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch("Shefield Magistrat Court");
        assertThat(results).hasSizeGreaterThanOrEqualTo(1);
        assertThat(results.get(0).getName()).isEqualTo(EXPECTED_COURT_NAME);
    }

    @Test
    void shouldFindCourtWithMisspeltWelshName() {
        final List<CourtReference> results = courtService.getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch("Caedydd");
        assertThat(results).hasSizeGreaterThanOrEqualTo(1);
        assertThat(results.get(0).getName()).contains("Cardiff");
    }

    @Test
    void shouldFindCourtWhereOnlyFirstWordOfCourtNameMatches() {
        final List<CourtReference> results = courtService.getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch("bradford magistrat");
        assertThat(results).hasSizeGreaterThanOrEqualTo(1);
        assertThat(results.get(0).getName()).contains("Bradford");
    }

    @Test
    void shouldNotFindCourtWithRandomWords() {
        final List<CourtReference> results = courtService.getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch("This should not match");
        assertThat(results).isEmpty();
    }
}
