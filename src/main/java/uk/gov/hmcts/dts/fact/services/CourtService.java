package uk.gov.hmcts.dts.fact.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.exception.SlugNotFoundException;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

@Service
public class CourtService {

    @Autowired
    CourtRepository courtRepository;

    public uk.gov.hmcts.dts.fact.model.Court getCourtBySlug(String slug) {
        return courtRepository
            .findBySlug(slug)
            .map(this::mapCourt)
            .orElseThrow(() -> new SlugNotFoundException(slug));
    }


    private uk.gov.hmcts.dts.fact.model.Court mapCourt(uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        return new uk.gov.hmcts.dts.fact.model.Court(courtEntity);
    }
}
