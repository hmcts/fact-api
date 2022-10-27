package uk.gov.hmcts.dts.fact.services.admin;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.exception.LockExistsException;
import uk.gov.hmcts.dts.fact.model.admin.CourtLock;
import uk.gov.hmcts.dts.fact.repositories.CourtLockRepository;

import java.util.List;

@Service
@Slf4j
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class AdminCourtLockService {
    private final CourtLockRepository courtLockRepository;
    private final AdminAuditService adminAuditService;

    @Autowired
    public AdminCourtLockService(final CourtLockRepository courtLockRepository,
                                 final AdminAuditService adminAuditService) {
        this.courtLockRepository = courtLockRepository;
        this.adminAuditService = adminAuditService;
    }

    public void addNewCourtLock(CourtLock courtLock) {
        // Check if the lock exists for the username/email
        List<uk.gov.hmcts.dts.fact.entity.CourtLock> courtLockEntityList = courtLockRepository.findByUserEmail(courtLock.getUserEmail());

        if (courtLockEntityList.size() > 0) {
            // If it doesn't, but we have a lock for the court, return with an error code
            throw new LockExistsException(String.format("Lock already exists for user: '%s' and court '%s'",
                                                        courtLock.getUserEmail(), courtLock.getCourtSlug()));
        } else {
            // If it exists, delete and update the resource
            courtLockRepository.save(new uk.gov.hmcts.dts.fact.entity.CourtLock(courtLock));
        }
    }


//    public void checkPostcodesExist(final String slug, final List<String> postcodes) {
//        checkPostcodesExist(getCourtPostcodes(slug, postcodes), postcodes);
//    }
//
//    private void checkPostcodesExist(List<CourtPostcode> sourceCourtPostcodes, List<String> postcodes) {
//        final List<String> sourcePostcodes = sourceCourtPostcodes.stream()
//            .map(CourtPostcode::getPostcode)
//            .collect(toList());
//        final List<String> invalidPostcodes = postcodes.stream()
//            .filter(p -> !sourcePostcodes.contains(upperCaseAndStripAllSpaces(p)))
//            .collect(toList());
//        if (!CollectionUtils.isEmpty(invalidPostcodes)) {
//            log.warn("Postcodes do not exist for specified slug (for requested deletion). "
//                         + "Postcodes not found are: {}", invalidPostcodes);
//            throw new PostcodeNotFoundException(invalidPostcodes);
//        }
//    }
//
//    public void checkPostcodesDoNotExist(final String destinationSlug, final List<String> postcodes) {
//        final List<String> destPostcodes = getCourtPostcodes(destinationSlug, postcodes).stream()
//            .map(CourtPostcode::getPostcode)
//            .collect(toList());
//        final List<String> duplicatedPostcodes = postcodes.stream()
//            .filter(p -> destPostcodes.contains(upperCaseAndStripAllSpaces(p)))
//            .collect(toList());
//        if (!CollectionUtils.isEmpty(duplicatedPostcodes)) {
//            log.warn("Postcodes already exist in destination table: {}", duplicatedPostcodes);
//            throw new PostcodeExistedException(duplicatedPostcodes);
//        }
//    }
//
//    public List<String> getCourtPostcodesBySlug(final String slug) {
//        return courtRepository.findBySlug(slug)
//            .map(c -> c.getCourtPostcodes()
//                .stream()
//                .map(CourtPostcode::getPostcode)
//                .collect(toList()))
//            .orElseThrow(() -> new NotFoundException(slug));
//    }
//
//    @Transactional()
//    public List<String> addCourtPostcodes(final String slug, final List<String> postcodes) {
//        final Court courtEntity = getCourtEntity(slug);
//        final List<String> originalPostcodes = courtEntity.getCourtPostcodes()
//            .stream()
//            .map(CourtPostcode::getPostcode)
//            .collect(toList());
//        List<String> newPostcodes = createNewCourtPostcodesEntity(courtEntity, postcodes).stream()
//            .filter(p -> !postcodeExists(
//                courtEntity,
//                p.getPostcode()
//            )) // If the same valid postcode entered more than once, we only add a single one
//            .map(courtPostcodeRepository::save)
//            .map(CourtPostcode::getPostcode)
//            .collect(toList());
//        adminAuditService.saveAudit(
//            AuditType.findByName("Create court postcodes"),
//            originalPostcodes,
//            newPostcodes, slug);
//        return newPostcodes;
//    }
//
//    @Transactional()
//    public int deleteCourtPostcodes(final String slug, final List<String> postcodes) {
//        final Court courtEntity = getCourtEntity(slug);
//        final List<String> originalPostcodes = courtEntity.getCourtPostcodes()
//            .stream()
//            .map(CourtPostcode::getPostcode)
//            .collect(toList());
//
//        int deletedPostcodes = postcodes.stream()
//            .map(p -> courtPostcodeRepository.deleteByCourtIdAndPostcode(
//                courtEntity.getId(),
//                upperCaseAndStripAllSpaces(p)
//            ))
//            .mapToInt(List::size)
//            .sum();
//
//        adminAuditService.saveAudit(
//            AuditType.findByName("Delete court postcodes"),
//            originalPostcodes,
//            getCourtPostcodesBySlug(slug),
//            null);
//        return deletedPostcodes;
//    }
//
//    @Transactional()
//    public List<String> moveCourtPostcodes(String sourceSlug, String destinationSlug, List<String> postcodes) {
//        // Check that the postcodes for the source court exists in the database, and retrieve the court id back
//        List<CourtPostcode> sourceCourtPostcodes = getCourtPostcodes(sourceSlug, postcodes);
//        checkPostcodesExist(sourceCourtPostcodes, postcodes);
//
//        // As a part of the above, check that the court where the postcodes will be moved to, do not already
//        // contain one or more postcodes from the source court
//        checkPostcodesDoNotExist(destinationSlug, postcodes);
//
//        // Move the postcodes from the source court, to the destination court, based on the
//        // destination courts court id
//        final Court destCourt = getCourtEntity(destinationSlug);
//        for (CourtPostcode courtPostcode : sourceCourtPostcodes) {
//            courtPostcode.setCourt(destCourt);
//        }
//        final List<String> postcodesMoved = courtPostcodeRepository.saveAll(sourceCourtPostcodes)
//            .stream()
//            .map(CourtPostcode::getPostcode)
//            .collect(toList());
//        JsonObject auditData = new JsonObject();
//        auditData.addProperty("moved-from", sourceSlug);
//        auditData.addProperty("moved-to", destinationSlug);
//        auditData.addProperty("postcodes", postcodes.toString());
//
//        adminAuditService.saveAudit(
//            AuditType.findByName("Move court postcodes"),
//            auditData,
//            postcodesMoved, sourceSlug);
//        return postcodesMoved;
//    }
//
//    private boolean postcodeExists(final Court court, final String postcode) {
//        return !courtPostcodeRepository.findByCourtIdAndPostcode(
//            court.getId(),
//            upperCaseAndStripAllSpaces(postcode)
//        ).isEmpty();
//    }
//
//    private List<CourtPostcode> createNewCourtPostcodesEntity(final Court court, final List<String> postcodes) {
//        return postcodes.stream()
//            .map(p -> new CourtPostcode(upperCaseAndStripAllSpaces(p), court))
//            .collect(toList());
//    }
//
//    private Court getCourtEntity(final String slug) {
//        Optional<Court> court = courtRepository.findBySlug(slug);
//        if (court.isPresent()) {
//            return court.get();
//        }
//        log.warn("Court slug {} not found", slug);
//        throw new NotFoundException(slug);
//    }
//
//    private List<CourtPostcode> getCourtPostcodes(String slug, List<String> postcodes) {
//        final Court courtEntity = getCourtEntity(slug);
//        final List<String> postcodesToRetrieve = postcodes.stream()
//            .map(Utils::upperCaseAndStripAllSpaces)
//            .collect(toList());
//        return courtPostcodeRepository.findByCourtIdAndPostcodeIn(courtEntity.getId(), postcodesToRetrieve);
//    }
}
