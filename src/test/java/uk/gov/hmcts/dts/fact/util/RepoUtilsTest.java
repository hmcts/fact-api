package uk.gov.hmcts.dts.fact.util;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.dts.fact.util.RepoUtils.checkIfCourtAlreadyExists;

@SuppressWarnings("PMD.LawOfDemeter")
public class RepoUtilsTest {

    private static final String TEST_SLUG = "test-slug";
    private final CourtRepository courtRepository = mock(CourtRepository.class);

    @Test
    void testCourtDoesNotExist() {
        when(courtRepository.findBySlug(TEST_SLUG))
            .thenReturn(java.util.Optional.empty());
        assertDoesNotThrow(() -> checkIfCourtAlreadyExists(courtRepository, "test-slug"));
    }

    @Test
    void testCourtExistsAndThrowsException() {
        when(courtRepository.findBySlug(TEST_SLUG))
            .thenReturn(java.util.Optional.of(new Court()));
        assertThatThrownBy(() -> checkIfCourtAlreadyExists(courtRepository, "test-slug"))
            .isInstanceOf(DuplicatedListItemException.class)
            .hasMessage("Court already exists with slug: " + TEST_SLUG);
        verify(courtRepository, atMostOnce()).findBySlug(TEST_SLUG);
    }
}
