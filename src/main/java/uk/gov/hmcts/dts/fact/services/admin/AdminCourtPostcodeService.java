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
import uk.gov.hmcts.dts.fact.util.AuditType;
import uk.gov.hmcts.dts.fact.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.util.Utils.upperCaseAndStripAllSpaces;

/**
 * Service for admin court postcode data.
 */
@Service
@Slf4j
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class AdminCourtPostcodeService {
    private final CourtRepository courtRepository;
    private final CourtPostcodeRepository courtPostcodeRepository;
    private final AdminAuditService adminAuditService;

    /**
     * Constructor for the AdminCourtPostcodeService.
     * @param courtRepository The repository for court
     * @param courtPostcodeRepository The repository for court postcode
     * @param adminAuditService The service for admin audit
     */
    @Autowired
    public AdminCourtPostcodeService(final CourtRepository courtRepository,
                                     final CourtPostcodeRepository courtPostcodeRepository,
                                     final AdminAuditService adminAuditService) {
        this.courtRepository = courtRepository;
        this.courtPostcodeRepository = courtPostcodeRepository;
        this.adminAuditService = adminAuditService;
    }

    /**
     * Check that the postcodes for the court exist by slug.
     * @param slug The slug of the court
     * @return The postcodes for the court
     */
    public void checkPostcodesExist(final String slug, final List<String> postcodes) {
        checkPostcodesExist(getCourtPostcodes(slug, postcodes), postcodes);
    }

    /**
     * Check that the postcodes for the source court exist in the database.
     * @param sourceCourtPostcodes The source court postcodes
     * @param postcodes The postcodes
     */
    private void checkPostcodesExist(List<CourtPostcode> sourceCourtPostcodes, List<String> postcodes) {
        final List<String> sourcePostcodes = sourceCourtPostcodes.stream()
            .map(CourtPostcode::getPostcode)
            .collect(toList());
        final List<String> invalidPostcodes = postcodes.stream()
            .filter(p -> !sourcePostcodes.contains(upperCaseAndStripAllSpaces(p)))
            .collect(toList());
        if (!CollectionUtils.isEmpty(invalidPostcodes)) {
            log.warn("Postcodes do not exist for specified slug (for requested deletion). "
                         + "Postcodes not found are: {}", invalidPostcodes);
            throw new PostcodeNotFoundException(invalidPostcodes);
        }
    }

    /**
     * Check that the list of postcodes do not exist for the destination court.
     * @param destinationSlug The destination slug
     * @param postcodes The postcodes
     */
    public void checkPostcodesDoNotExist(final String destinationSlug, final List<String> postcodes) {
        final List<String> destPostcodes = getCourtPostcodes(destinationSlug, postcodes).stream()
            .map(CourtPostcode::getPostcode)
            .collect(toList());
        final List<String> duplicatedPostcodes = postcodes.stream()
            .filter(p -> destPostcodes.contains(upperCaseAndStripAllSpaces(p)))
            .collect(toList());
        if (!CollectionUtils.isEmpty(duplicatedPostcodes)) {
            log.warn("Postcodes already exist in destination table: {}", duplicatedPostcodes);
            throw new PostcodeExistedException(duplicatedPostcodes);
        }
    }

    /**
     * Get the postcodes for a court by slug.
     * @param slug The slug of the court
     * @return The postcodes for the court
     */
    public List<String> getCourtPostcodesBySlug(final String slug) {
        return courtRepository.findBySlug(slug)
            .map(c -> c.getCourtPostcodes()
                .stream()
                .map(CourtPostcode::getPostcode)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    /**
     * Add postcodes to a court by slug.
     * @param slug The slug of the court
     * @param postcodes The postcodes to add
     * @return The postcodes added to the court
     */
    @Transactional()
    public List<String> addCourtPostcodes(final String slug, final List<String> postcodes) {
        final Court courtEntity = getCourtEntity(slug);
        final List<String> originalPostcodes = courtEntity.getCourtPostcodes()
            .stream()
            .map(CourtPostcode::getPostcode)
            .collect(toList());
        List<String> newPostcodes = createNewCourtPostcodesEntity(courtEntity, postcodes).stream()
            .filter(p -> !postcodeExists(
                courtEntity,
                p.getPostcode()
            )) // If the same valid postcode entered more than once, we only add a single one
            .map(courtPostcodeRepository::save)
            .map(CourtPostcode::getPostcode)
            .collect(toList());
        adminAuditService.saveAudit(
            AuditType.findByName("Create court postcodes"),
            originalPostcodes,
            newPostcodes, slug);
        return newPostcodes;
    }

    /**
     * Delete postcodes from a court by slug.
     * @param slug The slug of the court
     * @param postcodes The postcodes to delete
     * @return The number of postcodes deleted
     */
    @Transactional()
    public int deleteCourtPostcodes(final String slug, final List<String> postcodes) {
        final Court courtEntity = getCourtEntity(slug);
        final List<String> originalPostcodes = courtEntity.getCourtPostcodes()
            .stream()
            .map(CourtPostcode::getPostcode)
            .collect(toList());

        int deletedPostcodes = postcodes.stream()
            .map(p -> courtPostcodeRepository.deleteByCourtIdAndPostcode(
                courtEntity.getId(),
                upperCaseAndStripAllSpaces(p)
            ))
            .mapToInt(List::size)
            .sum();

        adminAuditService.saveAudit(
            AuditType.findByName("Delete court postcodes"),
            originalPostcodes,
            getCourtPostcodesBySlug(slug),
            null);
        return deletedPostcodes;
    }

    /**
     * Move postcodes from one court to another by slug.
     * @param sourceSlug The slug of the source court
     * @param destinationSlug The slug of the destination court
     * @param postcodes The postcodes to move
     * @return The postcodes moved
     */
    @Transactional()
    public List<String> moveCourtPostcodes(String sourceSlug, String destinationSlug, List<String> postcodes) {
        // Check that the postcodes for the source court exists in the database, and retrieve the court id back
        List<CourtPostcode> sourceCourtPostcodes = getCourtPostcodes(sourceSlug, postcodes);
        checkPostcodesExist(sourceCourtPostcodes, postcodes);

        // As a part of the above, check that the court where the postcodes will be moved to, do not already
        // contain one or more postcodes from the source court
        checkPostcodesDoNotExist(destinationSlug, postcodes);

        // Move the postcodes from the source court, to the destination court, based on the
        // destination courts court id
        final Court destCourt = getCourtEntity(destinationSlug);
        for (CourtPostcode courtPostcode : sourceCourtPostcodes) {
            courtPostcode.setCourt(destCourt);
        }
        final List<String> postcodesMoved = courtPostcodeRepository.saveAll(sourceCourtPostcodes)
            .stream()
            .map(CourtPostcode::getPostcode)
            .collect(toList());
        HashMap<String, String> auditData = new HashMap<>();
        auditData.put("moved-from", sourceSlug);
        auditData.put("moved-to", destinationSlug);
        auditData.put("postcodes", postcodes.toString());

        adminAuditService.saveAudit(
            AuditType.findByName("Move court postcodes"),
            auditData,
            postcodesMoved, sourceSlug);
        return postcodesMoved;
    }

    /**
     * Update postcodes for a court by slug.
     * @param slug The slug of the court
     * @param postcodes The postcodes to update
     * @return The updated postcodes for the court
     */
    private boolean postcodeExists(final Court court, final String postcode) {
        return !courtPostcodeRepository.findByCourtIdAndPostcode(
            court.getId(),
            upperCaseAndStripAllSpaces(postcode)
        ).isEmpty();
    }

    /**
     * Create new court postcodes entity.
     * @param court The court entity
     * @param postcodes The postcodes to create
     * @return The new court postcodes entity
     */
    private List<CourtPostcode> createNewCourtPostcodesEntity(final Court court, final List<String> postcodes) {
        return postcodes.stream()
            .map(p -> new CourtPostcode(upperCaseAndStripAllSpaces(p), court))
            .collect(toList());
    }

    /**
     * Get the court entity by slug.
     * @param slug The slug of the court
     * @return The court entity
     */
    private Court getCourtEntity(final String slug) {
        Optional<Court> court = courtRepository.findBySlug(slug);
        if (court.isPresent()) {
            return court.get();
        }
        log.warn("Court slug {} not found", slug);
        throw new NotFoundException(slug);
    }

    /**
     * Get the court postcodes by slug and postcodes.
     * @param slug The slug of the court
     * @param postcodes The postcodes
     * @return The court postcodes
     */
    private List<CourtPostcode> getCourtPostcodes(String slug, List<String> postcodes) {
        final Court courtEntity = getCourtEntity(slug);
        final List<String> postcodesToRetrieve = postcodes.stream()
            .map(Utils::upperCaseAndStripAllSpaces)
            .collect(toList());
        return courtPostcodeRepository.findByCourtIdAndPostcodeIn(courtEntity.getId(), postcodesToRetrieve);
    }
}
