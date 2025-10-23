package uk.gov.hmcts.dts.fact.migration.service;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.ContactType;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.LocalAuthority;
import uk.gov.hmcts.dts.fact.entity.OpeningType;
import uk.gov.hmcts.dts.fact.entity.Region;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.entity.ServiceAreaCourt;
import uk.gov.hmcts.dts.fact.migration.model.AreaOfLawTypeData;
import uk.gov.hmcts.dts.fact.migration.model.ContactDescriptionTypeData;
import uk.gov.hmcts.dts.fact.migration.model.CourtMigrationData;
import uk.gov.hmcts.dts.fact.migration.model.CourtServiceAreaData;
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

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public MigrationPrivateDataService(final CourtRepository courtRepository,
                                       final LocalAuthorityRepository localAuthorityRepository,
                                       final ServiceAreaRepository serviceAreaRepository,
                                       final ServiceRepository serviceRepository,
                                       final ContactTypeRepository contactTypeRepository,
                                       final OpeningTypeRepository openingTypeRepository,
                                       final CourtTypeRepository courtTypeRepository,
                                       final RegionRepository regionRepository,
                                       final AreasOfLawRepository areasOfLawRepository) {
        this.courtRepository = courtRepository;
        this.localAuthorityRepository = localAuthorityRepository;
        this.serviceAreaRepository = serviceAreaRepository;
        this.serviceRepository = serviceRepository;
        this.contactTypeRepository = contactTypeRepository;
        this.openingTypeRepository = openingTypeRepository;
        this.courtTypeRepository = courtTypeRepository;
        this.regionRepository = regionRepository;
        this.areasOfLawRepository = areasOfLawRepository;
    }

    public MigrationExportResponse getCourtExport() {
        List<CourtMigrationData> courts = courtRepository.findAll()
            .stream()
            .filter(court -> Boolean.TRUE.equals(court.getDisplayed()))
            .map(this::mapCourt)
            .collect(Collectors.toList());

        List<LocalAuthorityTypeData> localAuthorityTypes = localAuthorityRepository.findAll()
            .stream()
            .map(this::mapLocalAuthority)
            .collect(Collectors.toList());

        List<ServiceAreaTypeData> serviceAreas = serviceAreaRepository.findAll()
            .stream()
            .map(this::mapServiceArea)
            .collect(Collectors.toList());

        List<ServiceTypeData> services = serviceRepository.findAll()
            .stream()
            .map(this::mapService)
            .collect(Collectors.toList());

        List<ContactDescriptionTypeData> contactDescriptionTypes = contactTypeRepository.findAll()
            .stream()
            .map(this::mapContactType)
            .collect(Collectors.toList());

        List<OpeningHourTypeData> openingHourTypes = openingTypeRepository.findAll()
            .stream()
            .map(this::mapOpeningType)
            .collect(Collectors.toList());

        List<CourtTypeData> courtTypes = courtTypeRepository.findAll()
            .stream()
            .map(this::mapCourtType)
            .collect(Collectors.toList());

        List<RegionData> regions = regionRepository.findAll()
            .stream()
            .map(this::mapRegion)
            .collect(Collectors.toList());

        List<AreaOfLawTypeData> areaOfLawTypes = areasOfLawRepository.findAll()
            .stream()
            .map(this::mapAreaOfLaw)
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

    private CourtMigrationData mapCourt(final Court court) {
        return new CourtMigrationData(
            court.getId() == null ? null : court.getId().toString(),
            court.getName(),
            court.getSlug(),
            Boolean.TRUE.equals(court.getDisplayed()),
            toOffsetDateTime(court.getCreatedAt()),
            toOffsetDateTime(court.getUpdatedAt()),
            court.getRegionId(),
            court.getServiceCentre() != null,
            mapCourtServiceAreas(court)
        );
    }

    private LocalAuthorityTypeData mapLocalAuthority(final LocalAuthority localAuthority) {
        return new LocalAuthorityTypeData(localAuthority.getId(), localAuthority.getName());
    }

    private ServiceAreaTypeData mapServiceArea(final ServiceArea serviceArea) {
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

    private ServiceTypeData mapService(final uk.gov.hmcts.dts.fact.entity.Service service) {
        return new ServiceTypeData(
            service.getId(),
            service.getName(),
            service.getNameCy(),
            service.getDescription(),
            service.getDescriptionCy(),
            service.getSlug()
        );
    }

    private ContactDescriptionTypeData mapContactType(final ContactType contactType) {
        return new ContactDescriptionTypeData(
            contactType.getId(),
            contactType.getDescription(),
            contactType.getDescriptionCy()
        );
    }

    private OpeningHourTypeData mapOpeningType(final OpeningType openingType) {
        return new OpeningHourTypeData(
            openingType.getId(),
            openingType.getDescription(),
            openingType.getDescriptionCy()
        );
    }

    private CourtTypeData mapCourtType(final CourtType courtType) {
        return new CourtTypeData(
            courtType.getId(),
            courtType.getName(),
            courtType.getSearch()
        );
    }

    private RegionData mapRegion(final Region region) {
        return new RegionData(
            region.getId(),
            region.getName(),
            region.getCountry()
        );
    }

    private AreaOfLawTypeData mapAreaOfLaw(final AreaOfLaw areaOfLaw) {
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

    private List<CourtServiceAreaData> mapCourtServiceAreas(final Court court) {
        if (court.getServiceAreaCourts() == null || court.getServiceAreaCourts().isEmpty()) {
            return List.of();
        }

        Map<String, List<ServiceAreaCourt>> groupedByCatchment = court.getServiceAreaCourts()
            .stream()
            .collect(Collectors.groupingBy(
                ServiceAreaCourt::getCatchmentType,
                LinkedHashMap::new,
                Collectors.toList()
            ));

        return groupedByCatchment.entrySet()
            .stream()
            .map(entry -> {
                List<Integer> serviceAreaIds = entry.getValue()
                    .stream()
                    .map(ServiceAreaCourt::getServicearea)
                    .filter(Objects::nonNull)
                    .map(ServiceArea::getId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

                Integer id = entry.getValue()
                    .stream()
                    .map(ServiceAreaCourt::getId)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

                return new CourtServiceAreaData(
                    id,
                    serviceAreaIds,
                    court.getId(),
                    entry.getKey()
                );
            })
            .collect(Collectors.toList());
    }

    private OffsetDateTime toOffsetDateTime(final Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant().atOffset(ZoneOffset.UTC);
    }
}
