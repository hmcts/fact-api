package uk.gov.hmcts.dts.fact.services.admin;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtFacility;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.html.sanitizer.OwaspHtmlSanitizer;
import uk.gov.hmcts.dts.fact.model.admin.Facility;
import uk.gov.hmcts.dts.fact.repositories.CourtFacilityRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.FacilityTypeRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * Service for admin court facility data.
 */
@Service
public class AdminCourtFacilityService {
    private final CourtRepository courtRepository;
    private final CourtFacilityRepository courtFacilityRepository;
    private final FacilityTypeRepository facilityTypeRepository;
    private final AdminAuditService adminAuditService;

    /**
     * Constructor for the AdminCourtFacilityService.
     * @param courtRepository The repository for court
     * @param courtFacilityRepository The repository for court facility
     * @param facilityTypeRepository The repository for facility type
     * @param adminAuditService The service for admin audit
     */
    @Autowired
    public AdminCourtFacilityService(final CourtRepository courtRepository,
                                     final CourtFacilityRepository courtFacilityRepository,
                                     final FacilityTypeRepository facilityTypeRepository,
                                     final AdminAuditService adminAuditService) {
        this.courtRepository = courtRepository;
        this.courtFacilityRepository = courtFacilityRepository;
        this.facilityTypeRepository = facilityTypeRepository;
        this.adminAuditService = adminAuditService;
    }

    /**
     * Get all court facilities by slug.
     * @param slug The slug
     * @return A list of facilities
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public List<Facility> getCourtFacilitiesBySlug(final String slug) {
        return  courtRepository.findBySlug(slug)
            .map(c -> c.getFacilities()
                .stream()
                .map(Facility::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    /**
     * Update court facilities.
     * @param slug The slug
     * @param courtFacilities The court facilities
     * @return A list of facilities
     */
    @Transactional()
    public List<Facility> updateCourtFacility(final String slug, final List<Facility> courtFacilities) {

        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        courtFacilities.forEach(facility -> {
            facility.setDescription(StringEscapeUtils.unescapeHtml4(OwaspHtmlSanitizer.sanitizeHtml(
                StringEscapeUtils.unescapeHtml4(facility.getDescription())
            )));
            facility.setDescriptionCy(StringEscapeUtils.unescapeHtml4(OwaspHtmlSanitizer.sanitizeHtml(
                StringEscapeUtils.unescapeHtml4(facility.getDescriptionCy())
            )));
        });

        List<CourtFacility> existingList = getExistingCourtFacilities(courtEntity);
        List<Facility> newFacilities = saveCourtFacilities(courtEntity, courtFacilities, existingList);
        adminAuditService.saveAudit(
            AuditType.findByName("Update court facilities"),
            existingList.stream()
                            .map(CourtFacility::getFacility)
                            .map(Facility::new)
                            .collect(toList()),
            newFacilities, slug);
        return newFacilities;
    }

    /**
     * Save new court facilities.
     * @param courtEntity The court entity
     * @param courtFacilities The court facilities
     * @param existingList The existing list
     * @return A list of facilities
     */
    protected List<Facility> saveCourtFacilities(final Court courtEntity, final List<Facility> courtFacilities,
                                                 final List<CourtFacility> existingList) {

        final List<uk.gov.hmcts.dts.fact.entity.Facility> facilitiesEntities = getNewFacilityEntity(courtFacilities);

        final List<CourtFacility> courtFacilitiesEntities = getNewCourtFacilityEntity(courtEntity, facilitiesEntities);

        //remove existing court facilities and add updated facilities
        courtFacilityRepository.deleteAll(existingList);

        return courtFacilityRepository
            .saveAll(courtFacilitiesEntities)
            .stream()
            .map(CourtFacility::getFacility)
            .map(Facility::new)
            .collect(toList());
    }

    /**
     * Construct new facility entity.
     * @param facilities The facilities
     * @return A list of facilities
     */
    private List<uk.gov.hmcts.dts.fact.entity.Facility> getNewFacilityEntity(final List<Facility> facilities) {

        return facilities.stream()
            .map(f -> new uk.gov.hmcts.dts.fact.entity.Facility(f.getDescription(), f.getDescriptionCy(),
                                                                Objects.requireNonNull(facilityTypeRepository.findById(
                                                                    f.getId()).orElse(null))
            ))
            .collect(toList());
    }

    /**
     * Construct new court facility entity.
     * @param courtEntity The court entity
     * @param facilities The facilities
     * @return A list of court facilities
     */
    private List<CourtFacility> getNewCourtFacilityEntity(final Court courtEntity, List<uk.gov.hmcts.dts.fact.entity.Facility> facilities) {

        return facilities.stream()
            .map(f -> new CourtFacility(courtEntity,f)).collect(toList());
    }

    /**
     * Get existing court facilities.
     * @param courtEntity The court entity
     * @return A list of court facilities
     */
    private List<CourtFacility> getExistingCourtFacilities(final Court courtEntity) {
        return new ArrayList<>(courtFacilityRepository.findByCourtId(courtEntity.getId()));
    }

}
