package uk.gov.hmcts.dts.fact.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

@Service
public class CourtService {

    @Autowired
    CourtRepository courtRepository;

    public Court getCourtBySlug(String slug) {
        return courtRepository.findBySlug(slug);
    }
}
