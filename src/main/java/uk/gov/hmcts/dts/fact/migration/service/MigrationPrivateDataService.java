package uk.gov.hmcts.dts.fact.migration.service;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.ContactType;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.LocalAuthority;
import uk.gov.hmcts.dts.fact.entity.OpeningType;
import uk.gov.hmcts.dts.fact.entity.Region;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.migration.mapper.MigrationCourtDataMapper;
import uk.gov.hmcts.dts.fact.migration.model.AreaOfLawTypeData;
import uk.gov.hmcts.dts.fact.migration.model.ContactDescriptionTypeData;
import uk.gov.hmcts.dts.fact.migration.model.CourtMigrationData;
import uk.gov.hmcts.dts.fact.migration.model.CourtTypeData;
import uk.gov.hmcts.dts.fact.migration.model.LocalAuthorityTypeData;
import uk.gov.hmcts.dts.fact.migration.model.MigrationExportResponse;
import uk.gov.hmcts.dts.fact.migration.model.OpeningHourTypeData;
import uk.gov.hmcts.dts.fact.migration.model.RegionData;
import uk.gov.hmcts.dts.fact.migration.model.ServiceAreaTypeData;
import uk.gov.hmcts.dts.fact.migration.model.ServiceTypeData;
import uk.gov.hmcts.dts.fact.repositories.AreasOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.ContactTypeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtTypeRepository;
import uk.gov.hmcts.dts.fact.repositories.LocalAuthorityRepository;
import uk.gov.hmcts.dts.fact.repositories.OpeningTypeRepository;
import uk.gov.hmcts.dts.fact.repositories.RegionRepository;
import uk.gov.hmcts.dts.fact.repositories.ServiceAreaRepository;
import uk.gov.hmcts.dts.fact.repositories.ServiceRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for assembling the data exported by the private migration endpoint.
 */
@Service
public class MigrationPrivateDataService {

    private final CourtRepository courtRepository;
    private final LocalAuthorityRepository localAuthorityRepository;
    private final ServiceAreaRepository serviceAreaRepository;
    private final ServiceRepository serviceRepository;
    private final ContactTypeRepository contactTypeRepository;
    private final OpeningTypeRepository openingTypeRepository;
    private final CourtTypeRepository courtTypeRepository;
    private final RegionRepository regionRepository;
    private final AreasOfLawRepository areasOfLawRepository;
    private final MigrationCourtDataMapper migrationCourtDataMapper;

    public MigrationPrivateDataService(final CourtRepository courtRepository,
                                       final LocalAuthorityRepository localAuthorityRepository,
                                       final ServiceAreaRepository serviceAreaRepository,
                                       final ServiceRepository serviceRepository,
                                       final ContactTypeRepository contactTypeRepository,
                                       final OpeningTypeRepository openingTypeRepository,
                                       final CourtTypeRepository courtTypeRepository,
                                       final RegionRepository regionRepository,
                                       final AreasOfLawRepository areasOfLawRepository,
                                       final MigrationCourtDataMapper migrationCourtDataMapper) {
        this.courtRepository = courtRepository;
        this.localAuthorityRepository = localAuthorityRepository;
        this.serviceAreaRepository = serviceAreaRepository;
        this.serviceRepository = serviceRepository;
        this.contactTypeRepository = contactTypeRepository;
        this.openingTypeRepository = openingTypeRepository;
        this.courtTypeRepository = courtTypeRepository;
        this.regionRepository = regionRepository;
        this.areasOfLawRepository = areasOfLawRepository;
        this.migrationCourtDataMapper = migrationCourtDataMapper;
    }

    /**
     * Build the migration export payload by aggregating court and supporting reference data.
     * @return serialisable migration export response
     */
    public MigrationExportResponse getCourtExport() {
        List<CourtMigrationData> courts = courtRepository.findAll()
            .stream()
            .filter(court -> Boolean.TRUE.equals(court.getDisplayed()))
            .map(migrationCourtDataMapper::mapCourt)
            .collect(Collectors.toList());

        List<LocalAuthorityTypeData> localAuthorityTypes = localAuthorityRepository.findAll()
            .stream()
            .map(this::toLocalAuthorityTypeData)
            .collect(Collectors.toList());

        List<ServiceAreaTypeData> serviceAreas = serviceAreaRepository.findAll()
            .stream()
            .map(this::toServiceAreaTypeData)
            .collect(Collectors.toList());

        List<ServiceTypeData> services = serviceRepository.findAll()
            .stream()
            .map(this::toServiceTypeData)
            .collect(Collectors.toList());

        List<ContactDescriptionTypeData> contactDescriptionTypes = contactTypeRepository.findAll()
            .stream()
            .map(this::toContactDescriptionTypeData)
            .collect(Collectors.toList());

        List<OpeningHourTypeData> openingHourTypes = openingTypeRepository.findAll()
            .stream()
            .map(this::toOpeningHourTypeData)
            .collect(Collectors.toList());

        List<CourtTypeData> courtTypes = courtTypeRepository.findAll()
            .stream()
            .map(this::toCourtTypeData)
            .collect(Collectors.toList());

        List<RegionData> regions = regionRepository.findAll()
            .stream()
            .map(this::toRegionData)
            .collect(Collectors.toList());

        List<AreaOfLawTypeData> areaOfLawTypes = areasOfLawRepository.findAll()
            .stream()
            .map(this::toAreaOfLawTypeData)
            .collect(Collectors.toList());

        return new MigrationExportResponse(
            courts,
            localAuthorityTypes,
            serviceAreas,
            services,
            contactDescriptionTypes,
            openingHourTypes,
            courtTypes,
            regions,
            areaOfLawTypes
        );
    }

    /**
     * Convert a local authority entity into its export representation.
     * @param localAuthority the persisted local authority entity
     * @return serialisable local authority data
     */
    private LocalAuthorityTypeData toLocalAuthorityTypeData(final LocalAuthority localAuthority) {
        return new LocalAuthorityTypeData(localAuthority.getId(), localAuthority.getName());
    }

    /**
     * Convert a service area into the export metadata structure.
     * @param serviceArea the service area entity
     * @return export DTO used by migration clients
     */
    private ServiceAreaTypeData toServiceAreaTypeData(final ServiceArea serviceArea) {
        Integer areaOfLawId = serviceArea.getAreaOfLaw() == null ? null : serviceArea.getAreaOfLaw().getId();
        return new ServiceAreaTypeData(
            serviceArea.getId(),
            serviceArea.getName(),
            serviceArea.getNameCy(),
            serviceArea.getDescription(),
            serviceArea.getDescriptionCy(),
            serviceArea.getSlug(),
            serviceArea.getOnlineUrl(),
            serviceArea.getOnlineText(),
            serviceArea.getOnlineTextCy(),
            serviceArea.getType(),
            serviceArea.getText(),
            serviceArea.getTextCy(),
            serviceArea.getCatchmentMethod(),
            areaOfLawId
        );
    }

    /**
     * Convert a service entity into its export DTO.
     * @param service the service entity
     * @return service metadata for migration
     */
    private ServiceTypeData toServiceTypeData(final uk.gov.hmcts.dts.fact.entity.Service service) {
        return new ServiceTypeData(
            service.getId(),
            service.getName(),
            service.getNameCy(),
            service.getDescription(),
            service.getDescriptionCy(),
            service.getSlug()
        );
    }

    /**
     * Convert a contact type entity into its description export form.
     * @param contactType contact type entity
     * @return contact description metadata
     */
    private ContactDescriptionTypeData toContactDescriptionTypeData(final ContactType contactType) {
        return new ContactDescriptionTypeData(
            contactType.getId(),
            contactType.getDescription(),
            contactType.getDescriptionCy()
        );
    }

    /**
     * Convert an opening type entity into the export structure.
     * @param openingType opening hours entity
     * @return opening hours metadata
     */
    private OpeningHourTypeData toOpeningHourTypeData(final OpeningType openingType) {
        return new OpeningHourTypeData(
            openingType.getId(),
            openingType.getDescription(),
            openingType.getDescriptionCy()
        );
    }

    /**
     * Convert a court type entity into migration export data.
     * @param courtType court type entity
     * @return court type metadata
     */
    private CourtTypeData toCourtTypeData(final CourtType courtType) {
        return new CourtTypeData(
            courtType.getId(),
            courtType.getName(),
            courtType.getSearch()
        );
    }

    /**
     * Convert a region entity into its migration export representation.
     * @param region region entity
     * @return region metadata
     */
    private RegionData toRegionData(final Region region) {
        return new RegionData(
            region.getId(),
            region.getName(),
            region.getCountry()
        );
    }

    /**
     * Convert an area of law entity into the DTO used by migration clients.
     * @param areaOfLaw area of law entity
     * @return area of law metadata
     */
    private AreaOfLawTypeData toAreaOfLawTypeData(final AreaOfLaw areaOfLaw) {
        return new AreaOfLawTypeData(
            areaOfLaw.getId(),
            areaOfLaw.getName(),
            areaOfLaw.getExternalLink(),
            areaOfLaw.getExternalLinkCy(),
            areaOfLaw.getExternalLinkDescription(),
            areaOfLaw.getExternalLinkDescriptionCy(),
            areaOfLaw.getAltName(),
            areaOfLaw.getAltNameCy(),
            areaOfLaw.getDisplayName(),
            areaOfLaw.getDisplayNameCy(),
            areaOfLaw.getDisplayExternalLink()
        );
    }
}
