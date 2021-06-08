package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtPostcode;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.repositories.CourtPostcodeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtPostcodeService {
    private final CourtRepository courtRepository;
    private final CourtPostcodeRepository courtPostcodeRepository;

    @Autowired
    public AdminCourtPostcodeService(final CourtRepository courtRepository, final CourtPostcodeRepository courtPostcodeRepository) {
        this.courtRepository = courtRepository;
        this.courtPostcodeRepository = courtPostcodeRepository;
    }

    public List<String> getCourtPostcodesBySlug(final String slug) {
        return courtRepository.findBySlug(slug)
            .map(c -> c.getCourtPostcodes()
                .stream()
                .map(CourtPostcode::getPostcode)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    @Transactional()
    public List<String> updateCourtPostcodes(final String slug, final List<String> postcodes) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        final List<CourtPostcode> newCourtPostcodes = getNewCourtPostcodes(courtEntity, postcodes);

        courtPostcodeRepository.deleteAll(courtEntity.getCourtPostcodes());

        return courtPostcodeRepository.saveAll(newCourtPostcodes)
            .stream()
            .map(CourtPostcode::getPostcode)
            .collect(toList());
    }

    private List<CourtPostcode> getNewCourtPostcodes(final Court court, final List<String> postcodes) {
        return postcodes.stream()
            .map(p -> new CourtPostcode(p, court))
            .collect(toList());
    }
}
