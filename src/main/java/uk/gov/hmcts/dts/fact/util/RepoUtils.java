package uk.gov.hmcts.dts.fact.util;

import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

/**
 * The type Repo utils.
 */
public final class RepoUtils {

    private RepoUtils() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    /**
     * Check if court already exists.
     *
     * @param courtRepository the court repository
     * @param courtSlugToCheck the court slug to check
     */
    public static void checkIfCourtAlreadyExists(CourtRepository courtRepository, String courtSlugToCheck) {
        if (courtRepository.findBySlug(courtSlugToCheck).isPresent()) {
            throw new DuplicatedListItemException("Court already exists with slug: " + courtSlugToCheck);
        }
    }
}
