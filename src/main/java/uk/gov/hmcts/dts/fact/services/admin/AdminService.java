package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.config.security.RolesProvider;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtOpeningTime;
import uk.gov.hmcts.dts.fact.entity.InPerson;
import uk.gov.hmcts.dts.fact.entity.OpeningTime;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.entity.ServiceCentre;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.CourtForDownload;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfoUpdate;
import uk.gov.hmcts.dts.fact.repositories.AreasOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtHistoryRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.ServiceAreaRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;
import uk.gov.hmcts.dts.fact.util.RepoUtils;
import uk.gov.hmcts.dts.fact.util.Utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

/**
 * Service for admin court data.
 */
@Service
public class AdminService {

    private final CourtRepository courtRepository;
    private final RolesProvider rolesProvider;
    private final AdminAuditService adminAuditService;
    private final ServiceAreaRepository serviceAreaRepository;
    private final AreasOfLawRepository areasOfLawRepository;

    private final CourtHistoryRepository courtHistoryRepository;

    private static final String INTRO_PARAGRAPH = "This location services all of England and Wales for {serviceArea}. We do not provide an in-person service.";
    private static final String INTRO_PARAGRAPH_CY = "Mae’r lleoliad hwn yn gwasanaethu Cymru a Lloegr i gyd ar gyfer {serviceArea}. Nid ydym yn darparu gwasanaeth wyneb yn wyneb.";

    /**
     * Constructor for the AdminService.
     * @param courtRepository The repository for court
     * @param rolesProvider The provider for roles
     * @param adminAuditService The service for admin audit
     * @param serviceAreaRepository The repository for service area
     * @param areasOfLawRepository The repository for areas of law
     */
    @Autowired
    public AdminService(final CourtRepository courtRepository,
                        final RolesProvider rolesProvider,
                        final AdminAuditService adminAuditService,
                        final ServiceAreaRepository serviceAreaRepository,
                        final AreasOfLawRepository areasOfLawRepository,
                        CourtHistoryRepository courtHistoryRepository) {
        this.courtRepository = courtRepository;
        this.rolesProvider = rolesProvider;
        this.adminAuditService = adminAuditService;
        this.serviceAreaRepository = serviceAreaRepository;
        this.areasOfLawRepository = areasOfLawRepository;
        this.courtHistoryRepository = courtHistoryRepository;
    }

    /**
     * Get all court references.
     * @return The court references
     */
    public List<CourtReference> getAllCourtReferences() {
        return courtRepository
            .findAll()
            .stream()
            .map(CourtReference::new)
            .collect(toList());
    }

    /**
     * Get all courts for download.
     * @return The courts for download
     */
    public List<CourtForDownload> getAllCourtsForDownload() {
        return courtRepository
            .findAll()
            .stream()
            .map(CourtForDownload::new)
            .sorted(Comparator.comparing(CourtForDownload::getName))
            .collect(toList());
    }

    /**
     * Get court by slug.
     * @return The court
     */
    public Court getCourtBySlug(String slug) {
        return courtRepository
            .findBySlug(slug)
            .map(Court::new)
            .orElseThrow(() -> new NotFoundException(slug));
    }

    /**
     * Get court entity by slug.
     * @return The court
     */
    public uk.gov.hmcts.dts.fact.entity.Court getCourtEntityBySlug(String slug) {
        return courtRepository.findBySlug(slug).orElseThrow(() -> new NotFoundException(slug));
    }

    /**
     * Save the court.
     * @param slug The slug of the court
     * @param court The court
     * @return The saved court
     */
    public Court save(String slug, Court court) {
        uk.gov.hmcts.dts.fact.entity.Court courtEntity = getCourtEntityBySlug(slug);
        courtEntity.setAlert(court.getAlert());
        courtEntity.setAlertCy(court.getAlertCy());

        if (rolesProvider.getRoles().contains("fact-super-admin")) {
            courtEntity.setDisplayed(court.getOpen());
            courtEntity.setInfo(court.getInfo());
            courtEntity.setInfoCy(court.getInfoCy());
            if (courtEntity.getInPerson() != null && courtEntity.getInPerson().getIsInPerson()) {
                courtEntity.getInPerson().setAccessScheme(court.getAccessScheme());
            }
        }

        final List<OpeningTime> openingTimes =
            ofNullable(court.getOpeningTimes()).stream()
                .flatMap(Collection::stream)
                .map(o -> new OpeningTime(o.getType(), o.getTypeCy(), o.getHours()))
                .collect(toList());

        List<CourtOpeningTime> courtOpeningTimes = new ArrayList<>();
        for (int i = 0; i < openingTimes.size(); i++) {
            @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
            CourtOpeningTime courtOpeningTime = new CourtOpeningTime();
            courtOpeningTime.setCourt(courtEntity);
            courtOpeningTime.setOpeningTime(openingTimes.get(i));
            courtOpeningTime.setSort(i);
            courtOpeningTimes.add(courtOpeningTime);
        }
        if (courtEntity.getCourtOpeningTimes() == null) {
            courtEntity.setCourtOpeningTimes(courtOpeningTimes);
        } else {
            courtEntity.getCourtOpeningTimes().clear();
            courtEntity.getCourtOpeningTimes().addAll(courtOpeningTimes);
        }

        uk.gov.hmcts.dts.fact.entity.Court updatedCourt = courtRepository.save(courtEntity);
        Court updatedCourtModel = new Court(updatedCourt);
        adminAuditService.saveAudit(
            AuditType.findByName("Update court details"),
            court,
            updatedCourtModel, slug);
        return updatedCourtModel;
    }

    /**
     * Update multiple courts info.
     * @param info The new court info
     */
    @Transactional
    public void updateMultipleCourtsInfo(CourtInfoUpdate info) {
        courtRepository.updateInfoForSlugs(info.getCourts(), info.getInfo(), info.getInfoCy());
    }

    /**
     * Update court lat lon.
     * @param slug The slug of the court
     * @param lat The new latitude
     * @param lon The new longitude
     */
    @Transactional
    public void updateCourtLatLon(final String slug, final Double lat, final Double lon) {
        courtRepository.updateLatLonBySlug(slug, lat, lon);
    }

    /**
     * Update court region.
     * @param slug The slug of the court
     * @param region The new region
     */
    @Transactional
    public void updateCourtRegion(final String slug, final String region) {
        courtRepository.updateRegionBySlug(slug, region);
    }

    /**
     * Get court image.
     * @param slug The slug of the court
     * @return The court image
     */
    public String getCourtImage(final String slug) {
        final uk.gov.hmcts.dts.fact.entity.Court court =
            courtRepository.findBySlug(slug).orElseThrow(() -> new NotFoundException(slug));
        return court.getImageFile();
    }

    @Transactional
    public String updateCourtImage(final String slug, final String imageFile) {
        final Optional<uk.gov.hmcts.dts.fact.entity.Court> court =
            courtRepository.findBySlug(slug);
        if (court.isEmpty()) {
            throw new NotFoundException(slug);
        }
        court.get().setUpdatedAt(Timestamp.from(Instant.now()));
        return courtRepository.updateCourtImageBySlug(slug, imageFile);
    }

    /**
     * Add new court.
     * @param newCourtName The new court name
     * @param newCourtSlug The new court slug
     * @param serviceCentre The new service centre
     * @param lon The new longitude
     * @param lat The new latitude
     * @param serviceAreas The new service areas
     * @return The new court
     */
    @Transactional
    public Court addNewCourt(String newCourtName, String newCourtSlug, Boolean serviceCentre,
                             double lon, double lat, List<String> serviceAreas) {
        RepoUtils.checkIfCourtAlreadyExists(courtRepository, newCourtSlug);
        uk.gov.hmcts.dts.fact.entity.Court newCourt =
            new uk.gov.hmcts.dts.fact.entity.Court();
        newCourt.setName(newCourtName);
        newCourt.setSlug(newCourtSlug);
        newCourt.setDisplayed(true);
        newCourt.setHideAols(false);
        newCourt.setWelshEnabled(true);
        newCourt.setLon(lon);
        newCourt.setLat(lat);
        List<ServiceArea> serviceAreaList = serviceAreaRepository.findAllByNameIn(serviceAreas)
            .orElse(Collections.emptyList());
        newCourt.setServiceAreas(serviceAreaList);
        courtRepository.save(newCourt);

        if (Objects.equals(Boolean.TRUE, serviceCentre)) {
            List<String> serviceAreasCy = serviceAreaList.stream().map(ServiceArea::getNameCy).collect(toList());
            List<AreaOfLaw> areasOfLawList = areasOfLawRepository.findAllByNameIn(serviceAreas).orElse(Collections.emptyList());
            List<String> serviceAreaDisplayNames = new ArrayList<>();
            List<String> serviceAreaDisplayNamesCy = new ArrayList<>();

            if (areasOfLawList.isEmpty()) {
                serviceAreaDisplayNames.addAll(serviceAreas);
            } else {
                for (String serviceArea : serviceAreas) {
                    serviceAreaDisplayNames.add(getDisplayName(areasOfLawList, serviceArea));
                }
            }

            for (String serviceAreaCy : serviceAreasCy) {
                serviceAreaDisplayNamesCy.add(getDisplayNameCy(areasOfLawList, serviceAreaCy));
            }

            ServiceCentre newServiceCentre = new ServiceCentre();
            newServiceCentre.setCourtId(newCourt);
            newServiceCentre.setIntroParagraph(INTRO_PARAGRAPH.replace("{serviceArea}",
                                                                Utils.formatServiceAreasForIntroPara(serviceAreaDisplayNames, "en")));
            newServiceCentre.setIntroParagraphCy(INTRO_PARAGRAPH_CY.replace("{serviceArea}",
                                                                  Utils.formatServiceAreasForIntroPara(serviceAreaDisplayNamesCy, "cy")));
            newCourt.setServiceCentre(newServiceCentre);
        }

        // By default the court will be in person unless the "service centre" flag is ticked
        // in which case we can skip this, as it will then default to false
        InPerson inPerson = new InPerson();
        inPerson.setIsInPerson(!serviceCentre);
        inPerson.setAccessScheme(false);
        inPerson.setCommonPlatform(false);

        // Link the search_court and search_inperson tables together through the court_id column relationship
        inPerson.setCourtId(newCourt);
        newCourt.setInPerson(inPerson);

        Court createdCourtModel = new Court(courtRepository.save(newCourt));

        adminAuditService.saveAudit(
            AuditType.findByName("Create new court"),
            null,
            createdCourtModel, newCourtSlug);

        return createdCourtModel;
    }

    /**
     * Delete court and its history.
     * @param courtSlug The slug of the court
     */
    @Transactional
    public void deleteCourt(String courtSlug) {
        uk.gov.hmcts.dts.fact.entity.Court court =
            courtRepository.findBySlug(courtSlug).orElseThrow(() -> new NotFoundException(courtSlug));

        adminAuditService.saveAudit(AuditType.findByName("Delete existing court"), new Court(court),
                                    null, courtSlug);
        courtHistoryRepository.deleteCourtHistoriesBySearchCourtId(court.getId());
        courtRepository.deleteById(court.getId());
    }

    /**
     * Get display name for service area.
     * @param areasOfLaw The areas of law
     * @param serviceArea The service area
     * @return The display name
     */
    private String getDisplayName(List<AreaOfLaw> areasOfLaw, String serviceArea) {
        return areasOfLaw.stream()
            .filter(areaOfLaw -> areaOfLaw.getName().equals(serviceArea))
            .findAny()
            .map(AreaOfLaw::getDisplayName)
            .orElse(serviceArea);
    }

    /**
     * Get display name cy for service area.
     * @param areasOfLaw The areas of law
     * @param serviceAreaCy The service area cy
     * @return The display name cy
     */
    private String getDisplayNameCy(List<AreaOfLaw> areasOfLaw, String serviceAreaCy) {
        return areasOfLaw.stream()
            .filter(areaOfLaw -> areaOfLaw.getName().equals(serviceAreaCy))
            .findAny()
            .map(AreaOfLaw::getDisplayNameCy)
            .orElse(serviceAreaCy);
    }
}
