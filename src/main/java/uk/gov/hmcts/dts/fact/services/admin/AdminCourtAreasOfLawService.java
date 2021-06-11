package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtAreasOfLawService {

    private final CourtRepository courtRepository;

    @Autowired
    public AdminCourtAreasOfLawService(final CourtRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    public List<AreaOfLaw> getCourtAreasOfLawBySlug(final String slug) {
        return courtRepository.findBySlug(slug)
            .map(c -> c.getAreasOfLaw()
            .stream()
            .map(AreaOfLaw::new)
            .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

}



