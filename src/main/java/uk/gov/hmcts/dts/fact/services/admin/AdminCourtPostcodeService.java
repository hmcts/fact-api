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
import java.util.stream.Collectors;

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
    public List<String> addCourtPostcodes(final String slug, final List<String> postcodes) {
        return createNewCourtPostcodesEntity(slug, postcodes).stream()
            .map(courtPostcodeRepository::save)
            .map(CourtPostcode::getPostcode)
            .collect(toList());
    }

    @Transactional()
    public void deleteCourtPostcodes(final String slug, final List<String> postcodes) {
        final List<CourtPostcode> courtPostcodesToBeDeleted = postcodes.stream()
            .map(p -> getCourtPostcodeEntity(slug, p))
            .collect(toList());
        courtPostcodeRepository.deleteAll(courtPostcodesToBeDeleted);
    }

    private CourtPostcode getCourtPostcodeEntity(final String slug, final String postcode) {
        final CourtPostcode courtPostcode = courtPostcodeRepository.findByCourtIdAndPostcode(getCourtEntity(slug).getId(), postcode);
        if (courtPostcode == null) {
            throw new NotFoundException(postcode);
        }
        return courtPostcode;
    }

    private List<CourtPostcode> createNewCourtPostcodesEntity(final String slug, final List<String> postcodes) {
        return postcodes.stream()
            .map(p -> new CourtPostcode(p, getCourtEntity(slug)))
            .collect(toList());
    }

    private Court getCourtEntity(final String slug) {
        return courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
    }
}
