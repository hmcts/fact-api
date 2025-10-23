package uk.gov.hmcts.dts.fact.migration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Contact;
import uk.gov.hmcts.dts.fact.entity.ContactType;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtAreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtAreaOfLawSpoe;
import uk.gov.hmcts.dts.fact.entity.CourtContact;
import uk.gov.hmcts.dts.fact.entity.CourtDxCode;
import uk.gov.hmcts.dts.fact.entity.CourtPostcode;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.DxCode;
import uk.gov.hmcts.dts.fact.entity.LocalAuthority;
import uk.gov.hmcts.dts.fact.entity.OpeningType;
import uk.gov.hmcts.dts.fact.entity.Region;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.entity.ServiceAreaCourt;
import uk.gov.hmcts.dts.fact.migration.model.AreaOfLawTypeData;
import uk.gov.hmcts.dts.fact.migration.model.ContactDescriptionTypeData;
import uk.gov.hmcts.dts.fact.migration.model.CourtAreasOfLawData;
import uk.gov.hmcts.dts.fact.migration.model.CourtCodeData;
import uk.gov.hmcts.dts.fact.migration.model.CourtDxCodeData;
import uk.gov.hmcts.dts.fact.migration.model.CourtFaxData;
import uk.gov.hmcts.dts.fact.migration.model.CourtMigrationData;
import uk.gov.hmcts.dts.fact.migration.model.CourtPhotoData;
import uk.gov.hmcts.dts.fact.migration.model.CourtPostcodeData;
import uk.gov.hmcts.dts.fact.migration.model.CourtServiceAreaData;
import uk.gov.hmcts.dts.fact.migration.model.CourtSinglePointOfEntryData;
import uk.gov.hmcts.dts.fact.migration.model.CourtTypeData;
import uk.gov.hmcts.dts.fact.migration.model.LocalAuthorityTypeData;
import uk.gov.hmcts.dts.fact.migration.model.MigrationExportResponse;
import uk.gov.hmcts.dts.fact.migration.model.OpeningHourTypeData;
import uk.gov.hmcts.dts.fact.migration.model.RegionData;
import uk.gov.hmcts.dts.fact.migration.model.ServiceAreaTypeData;
import uk.gov.hmcts.dts.fact.migration.model.ServiceTypeData;
import uk.gov.hmcts.dts.fact.repositories.AreasOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.ContactTypeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtAreaOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtAreaOfLawSpoeRepository;
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
    private final CourtAreaOfLawRepository courtAreaOfLawRepository;
    private final CourtAreaOfLawSpoeRepository courtAreaOfLawSpoeRepository;
    private final String imageBaseUrl;

    public MigrationPrivateDataService(final CourtRepository courtRepository,
                                       final LocalAuthorityRepository localAuthorityRepository,
                                       final ServiceAreaRepository serviceAreaRepository,
                                       final ServiceRepository serviceRepository,
                                       final ContactTypeRepository contactTypeRepository,
                                       final OpeningTypeRepository openingTypeRepository,
                                       final CourtTypeRepository courtTypeRepository,
                                       final RegionRepository regionRepository,
                                       final AreasOfLawRepository areasOfLawRepository,
                                       final CourtAreaOfLawRepository courtAreaOfLawRepository,
                                       final CourtAreaOfLawSpoeRepository courtAreaOfLawSpoeRepository,
                                       @Value("${storageAccount.imageUrl}") final String imageBaseUrl) {
        this.courtRepository = courtRepository;
        this.localAuthorityRepository = localAuthorityRepository;
        this.serviceAreaRepository = serviceAreaRepository;
        this.serviceRepository = serviceRepository;
        this.contactTypeRepository = contactTypeRepository;
        this.openingTypeRepository = openingTypeRepository;
        this.courtTypeRepository = courtTypeRepository;
        this.regionRepository = regionRepository;
        this.areasOfLawRepository = areasOfLawRepository;
        this.courtAreaOfLawRepository = courtAreaOfLawRepository;
        this.courtAreaOfLawSpoeRepository = courtAreaOfLawSpoeRepository;
        this.imageBaseUrl = imageBaseUrl;
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
            court.getAlert(),
            toOffsetDateTime(court.getCreatedAt()),
            toOffsetDateTime(court.getUpdatedAt()),
            court.getRegionId(),
            court.getServiceCentre() != null,
            mapCourtServiceAreas(court),
            mapCourtPostcodes(court),
            mapCourtCodes(court),
            mapCourtAreasOfLaw(court),
            mapCourtSinglePointsOfEntry(court),
            mapCourtDxCodes(court),
            mapCourtFaxNumbers(court),
            mapCourtPhoto(court)
        );
    }

    private LocalAuthorityTypeData mapLocalAuthority(final LocalAuthority localAuthority) {
        return new LocalAuthorityTypeData(localAuthority.getId(), localAuthority.getName());
    }

    private List<CourtPostcodeData> mapCourtPostcodes(final Court court) {
        List<CourtPostcode> courtPostcodes = court.getCourtPostcodes();
        if (courtPostcodes == null || courtPostcodes.isEmpty()) {
            return null;
        }

        return courtPostcodes
            .stream()
            .filter(Objects::nonNull)
            .map(courtPostcode -> new CourtPostcodeData(
                courtPostcode.getId(),
                courtPostcode.getPostcode(),
                courtPostcode.getCourt() == null ? null : courtPostcode.getCourt().getId()
            ))
            .collect(Collectors.toList());
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

    private CourtCodeData mapCourtCodes(final Court court) {
        String courtId = court.getId() == null ? null : court.getId().toString();
        if (court.getMagistrateCode() == null
            && court.getCourtCode() == null
            && court.getLocationCode() == null
            && court.getCciCode() == null
            && court.getNumber() == null
            && court.getGbs() == null) {
            return null;
        }

        return new CourtCodeData(
            courtId,
            courtId,
            court.getMagistrateCode(),
            court.getCourtCode(),
            court.getLocationCode(),
            court.getCciCode(),
            court.getNumber(),
            court.getGbs()
        );
    }

    private List<CourtServiceAreaData> mapCourtServiceAreas(final Court court) {
        List<ServiceAreaCourt> serviceAreaCourts = court.getServiceAreaCourts();
        if (serviceAreaCourts == null || serviceAreaCourts.isEmpty()) {
            return null;
        }

        Map<String, List<ServiceAreaCourt>> groupedByCatchment = serviceAreaCourts
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

    private CourtAreasOfLawData mapCourtAreasOfLaw(final Court court) {
        Integer courtId = court.getId();
        String courtIdAsString = courtId == null ? null : courtId.toString();

        List<CourtAreaOfLaw> courtAreas = courtId == null
            ? List.of()
            : courtAreaOfLawRepository.getCourtAreaOfLawByCourtId(courtId);

        if (courtAreas == null || courtAreas.isEmpty()) {
            return null;
        }

        List<Integer> areaIds = courtAreas.stream()
            .map(CourtAreaOfLaw::getAreaOfLaw)
            .filter(Objects::nonNull)
            .map(AreaOfLaw::getId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        String id = courtAreas.stream()
            .map(CourtAreaOfLaw::getId)
            .filter(Objects::nonNull)
            .map(String::valueOf)
            .findFirst()
            .orElse(null);

        return new CourtAreasOfLawData(id, areaIds, courtIdAsString);
    }

    private CourtSinglePointOfEntryData mapCourtSinglePointsOfEntry(final Court court) {
        Integer courtId = court.getId();
        String courtIdAsString = courtId == null ? null : courtId.toString();

        List<CourtAreaOfLawSpoe> courtAreas = courtId == null
            ? List.of()
            : courtAreaOfLawSpoeRepository.getAllByCourtId(courtId);

        if (courtAreas == null || courtAreas.isEmpty()) {
            return null;
        }

        List<Integer> areaIds = courtAreas.stream()
            .map(CourtAreaOfLawSpoe::getAreaOfLaw)
            .filter(Objects::nonNull)
            .map(AreaOfLaw::getId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        String id = courtAreas.stream()
            .map(CourtAreaOfLawSpoe::getId)
            .filter(Objects::nonNull)
            .map(String::valueOf)
            .findFirst()
            .orElse(null);

        return new CourtSinglePointOfEntryData(id, areaIds, courtIdAsString);
    }

    private List<CourtFaxData> mapCourtFaxNumbers(final Court court) {
        List<CourtContact> courtContacts = court.getCourtContacts();
        if (courtContacts == null || courtContacts.isEmpty()) {
            return null;
        }

        List<CourtFaxData> faxData = courtContacts.stream()
            .map(CourtContact::getContact)
            .filter(Objects::nonNull)
            .filter(Contact::isFax)
            .map(contact -> new CourtFaxData(
                contact.getId() == null ? null : contact.getId().toString(),
                court.getId() == null ? null : court.getId().toString(),
                contact.getNumber()
            ))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return faxData.isEmpty() ? null : faxData;
    }

    private CourtPhotoData mapCourtPhoto(final Court court) {
        String imageFile = court.getImageFile();
        if (imageFile == null || imageFile.isBlank()) {
            return null;
        }

        String base = imageBaseUrl == null ? "" : imageBaseUrl;
        String normalizedBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        String normalizedFile = imageFile.startsWith("/") ? imageFile.substring(1) : imageFile;
        return new CourtPhotoData(normalizedBase + "/" + normalizedFile);
    }

    private List<CourtDxCodeData> mapCourtDxCodes(final Court court) {
        List<CourtDxCode> courtDxCodes = court.getCourtDxCodes();
        if (courtDxCodes == null || courtDxCodes.isEmpty()) {
            return null;
        }

        List<CourtDxCodeData> dxCodes = courtDxCodes.stream()
            .map(courtDxCode -> {
                DxCode dxCode = courtDxCode.getDxCode();
                if (dxCode == null) {
                    return null;
                }
                return new CourtDxCodeData(
                    dxCode.getId() == null ? null : dxCode.getId().toString(),
                    court.getId() == null ? null : court.getId().toString(),
                    dxCode.getCode(),
                    dxCode.getExplanation()
                );
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return dxCodes.isEmpty() ? null : dxCodes;
    }

    private OffsetDateTime toOffsetDateTime(final Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant().atOffset(ZoneOffset.UTC);
    }
}
