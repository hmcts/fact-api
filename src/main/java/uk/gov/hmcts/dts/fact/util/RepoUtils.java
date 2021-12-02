package uk.gov.hmcts.dts.fact.util;

import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

public final class RepoUtils {

    protected RepoUtils() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public static void checkIfCourtAlreadyExists(CourtRepository courtRepository, String courtSlugToCheck) {
        if (courtRepository.findBySlug(courtSlugToCheck).isPresent()) {
            throw new DuplicatedListItemException("Court already exists with slug: " + courtSlugToCheck);
        }
    }
}
