package uk.gov.hmcts.dts.fact.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.List;

@Service
public class CourtService {

    @Autowired
    CourtRepository courtRepository;

    public List<Court> getCourts() {
        return courtRepository.findAll();
    }

    public Court getCourtBySlug(String slug) {
        return courtRepository.findBySlug(slug);
    }


}
