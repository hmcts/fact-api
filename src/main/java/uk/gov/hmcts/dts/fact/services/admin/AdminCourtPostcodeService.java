package uk.gov.hmcts.dts.fact.services.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtPostcode;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.exception.PostcodeExistedException;
import uk.gov.hmcts.dts.fact.exception.PostcodeNotFoundException;
import uk.gov.hmcts.dts.fact.repositories.CourtPostcodeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class AdminCourtPostcodeService {
    private final CourtRepository courtRepository;
    private final CourtPostcodeRepository courtPostcodeRepository;

    @Autowired
    public AdminCourtPostcodeService(final CourtRepository courtRepository, final CourtPostcodeRepository courtPostcodeRepository) {
        this.courtRepository = courtRepository;
        this.courtPostcodeRepository = courtPostcodeRepository;
    }

    public void checkPostcodesExist(final String slug, final List<String> postcodes) {
        final Court courtEntity = getCourtEntity(slug);
        final List<String> invalidPostcodes = postcodes.stream()
            .filter(postcode -> courtPostcodeRepository.findByCourtIdAndPostcode(courtEntity.getId(), postcode).isEmpty())
            .collect(toList());
        if (!CollectionUtils.isEmpty(invalidPostcodes)) {
            log.warn("Postcodes do not exist in database: {}", invalidPostcodes);
            throw new PostcodeNotFoundException(invalidPostcodes);
        }
    }

    public void checkPostcodesDoNotExist(final String slug, final List<String> postcodes) {
        final Court courtEntity = getCourtEntity(slug);
        final List<String> invalidPostcodes = postcodes.stream()
            .filter(postcode -> !courtPostcodeRepository.findByCourtIdAndPostcode(courtEntity.getId(), postcode).isEmpty())
            .collect(toList());
        if (!CollectionUtils.isEmpty(invalidPostcodes)) {
            log.warn("Postcodes already exist in database: {}", invalidPostcodes);
            throw new PostcodeExistedException(invalidPostcodes);
        }
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
    public int deleteCourtPostcodes(final String slug, final List<String> postcodes) {
        final Court courtEntity = getCourtEntity(slug);
        return postcodes.stream()
            .map(p -> courtPostcodeRepository.deleteByCourtIdAndPostcode(courtEntity.getId(), p))
            .mapToInt(List::size)
            .sum();
    }

    private List<CourtPostcode> createNewCourtPostcodesEntity(final String slug, final List<String> postcodes) {
        final Court courtEntity = getCourtEntity(slug);
        return postcodes.stream()
            .map(p -> new CourtPostcode(p, courtEntity))
            .collect(toList());
    }

    private Court getCourtEntity(final String slug) {
        Optional<Court> court = courtRepository.findBySlug(slug);
        if (court.isPresent()) {
            return court.get();
        }
        log.warn("Court slug {} not found", slug);
        throw new NotFoundException(slug);
    }
}
