package uk.gov.hmcts.dts.fact.services.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtPostcode;
import uk.gov.hmcts.dts.fact.exception.InvalidPostcodeException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.exception.PostcodeExistedException;
import uk.gov.hmcts.dts.fact.exception.PostcodeNotFoundException;
import uk.gov.hmcts.dts.fact.repositories.CourtPostcodeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.util.Utils.upperCaseAndStripAllSpaces;

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
            .filter(p -> !postcodeExists(courtEntity, p))
            .collect(toList());
        if (!CollectionUtils.isEmpty(invalidPostcodes)) {
            log.warn("Postcodes do not exist in database: {}", invalidPostcodes);
            throw new PostcodeNotFoundException(invalidPostcodes);
        }
    }

    public void checkPostcodesDoNotExist(final String slug, final List<String> postcodes) {
        final Court courtEntity = getCourtEntity(slug);
        final List<String> invalidPostcodes = postcodes.stream()
            .filter(p -> postcodeExists(courtEntity, p))
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
        final Court courtEntity = getCourtEntity(slug);
        return createNewCourtPostcodesEntity(courtEntity, postcodes).stream()
            .filter(p -> !postcodeExists(
                courtEntity,
                p.getPostcode()
            )) // If the same valid postcode entered more than once, we only add a single one
            .map(courtPostcodeRepository::save)
            .map(CourtPostcode::getPostcode)
            .collect(toList());
    }

    @Transactional()
    public int deleteCourtPostcodes(final String slug, final List<String> postcodes) {
        final Court courtEntity = getCourtEntity(slug);
        return postcodes.stream()
            .map(p -> courtPostcodeRepository.deleteByCourtIdAndPostcode(
                courtEntity.getId(),
                upperCaseAndStripAllSpaces(p)
            ))
            .mapToInt(List::size)
            .sum();
    }

    private boolean postcodeExists(final Court court, final String postcode) {
        return !courtPostcodeRepository.findByCourtIdAndPostcode(
            court.getId(),
            upperCaseAndStripAllSpaces(postcode)
        ).isEmpty();
    }

    private List<CourtPostcode> createNewCourtPostcodesEntity(final Court court, final List<String> postcodes) {
        return postcodes.stream()
            .map(p -> new CourtPostcode(upperCaseAndStripAllSpaces(p), court))
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

    public List<String> movePostcodes(String sourceSlug, String destinationSlug, List<String> postcodes) {

        // Check that the postcodes for the source court exists in the database, and retrieve the court id back
        List<CourtPostcode> sourceCourtPostcodes = getCourtPostcodes(sourceSlug, postcodes);
        if (postcodes.size() != sourceCourtPostcodes.size()) {
            throw new InvalidPostcodeException("Postcodes sent do not match");
        }

        // As a part of the above, check that the court where the postcodes will be moved to, do not already
        // contain one or more postcodes from the source court
        List<CourtPostcode> destCourtPostcodes = getCourtPostcodes(destinationSlug, postcodes);
        List<String> duplicatedPostcodes =
            destCourtPostcodes.stream().map(CourtPostcode::getPostcode).filter(postcodes::contains).collect(toList());
        if (duplicatedPostcodes.size() > 0) {
            throw new PostcodeExistedException(duplicatedPostcodes);
        }

        // Move the postcodes from the source court, to the destination court, based on the
        // destination courts court id
        final Court destCourt = getCourtEntity(destinationSlug);
        for (CourtPostcode courtPostcode : sourceCourtPostcodes) {
            courtPostcode.setCourt(destCourt);
        }
        return courtPostcodeRepository.saveAll(sourceCourtPostcodes).stream().map(CourtPostcode::getPostcode).collect(toList());
    }

    private List<CourtPostcode> getCourtPostcodes(String slug, List<String> postcodes) {
        final Court courtEntity = getCourtEntity(slug);
        return courtPostcodeRepository.findByCourtIdAndPostcodeIn(courtEntity.getId(), postcodes);
    }
}
