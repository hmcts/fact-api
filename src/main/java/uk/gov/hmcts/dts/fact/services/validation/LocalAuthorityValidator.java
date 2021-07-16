package uk.gov.hmcts.dts.fact.services.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.services.MapitService;

@Slf4j
@Component
public class LocalAuthorityValidator {

    private final MapitService mapitService;

    @Autowired
    public LocalAuthorityValidator(MapitService mapitService) {
        this.mapitService = mapitService;
    }

    public boolean localAuthorityNameIsValid(final String localAuthorityName) {
        Boolean localAuthorityExists = this.mapitService.localAuthorityExists(localAuthorityName);

        log.info((localAuthorityExists
            ? "Mapit data exists for local authority: "
            : "Mapit data does not exist for local authority: ") + localAuthorityName);

        return localAuthorityExists;
    }
}

