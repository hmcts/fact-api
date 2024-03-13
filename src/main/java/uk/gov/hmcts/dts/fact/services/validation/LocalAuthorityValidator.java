package uk.gov.hmcts.dts.fact.services.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.services.MapitService;

import java.util.Objects;

/**
 * Validates local authority names.
 */
@Slf4j
@Component
public class LocalAuthorityValidator {

    private final MapitService mapitService;

    /**
     * Constructor for the LocalAuthorityValidator.
     */
    @Autowired
    public LocalAuthorityValidator(MapitService mapitService) {
        this.mapitService = mapitService;
    }

    /**
     * Checks if a local authority name is valid.
     * @param localAuthorityName the local authority name to check
     * @return true if the local authority name is valid, false otherwise
     */
    public boolean localAuthorityNameIsValid(final String localAuthorityName) {
        Boolean localAuthorityExists = this.mapitService.localAuthorityExists(localAuthorityName);

        log.info((Objects.equals(Boolean.TRUE,localAuthorityExists)
            ? "Mapit data exists for local authority: "
            : "Mapit data does not exist for local authority: ") + localAuthorityName);

        return localAuthorityExists;
    }
}

