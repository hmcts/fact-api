package uk.gov.hmcts.dts.fact.migration.mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Contact;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtAreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtAreaOfLawSpoe;
import uk.gov.hmcts.dts.fact.entity.CourtContact;
import uk.gov.hmcts.dts.fact.entity.CourtDxCode;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.entity.ServiceAreaCourt;
import uk.gov.hmcts.dts.fact.migration.model.CourtAreasOfLawData;
import uk.gov.hmcts.dts.fact.migration.model.CourtCodeData;
import uk.gov.hmcts.dts.fact.migration.model.CourtDxCodeData;
import uk.gov.hmcts.dts.fact.migration.model.CourtFaxData;
import uk.gov.hmcts.dts.fact.migration.model.CourtMigrationData;
import uk.gov.hmcts.dts.fact.migration.model.CourtPhotoData;
import uk.gov.hmcts.dts.fact.migration.model.CourtPostcodeData;
import uk.gov.hmcts.dts.fact.migration.model.CourtServiceAreaData;
import uk.gov.hmcts.dts.fact.migration.model.CourtSinglePointOfEntryData;
import uk.gov.hmcts.dts.fact.repositories.CourtAreaOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtAreaOfLawSpoeRepository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Maps court entities and related database records into the migration DTO graph used by the export endpoint.
 */
@Component
public class MigrationCourtDataMapper {

    private static final char PATH_SEPARATOR_CHAR = '/';

    private final CourtAreaOfLawRepository courtAreaOfLawRepository;
    private final CourtAreaOfLawSpoeRepository courtAreaOfLawSpoeRepository;
    private final String imageBaseUrl;

    public MigrationCourtDataMapper(final CourtAreaOfLawRepository courtAreaOfLawRepository,
                                    final CourtAreaOfLawSpoeRepository courtAreaOfLawSpoeRepository,
                                    @Value("${storageAccount.imageUrl}") final String imageBaseUrl) {
        this.courtAreaOfLawRepository = courtAreaOfLawRepository;
        this.courtAreaOfLawSpoeRepository = courtAreaOfLawSpoeRepository;
        this.imageBaseUrl = imageBaseUrl;
    }

    /**
     * Convert a court entity into the migration export data structure.
     * @param court the court entity with its associations
     * @return populated {@link CourtMigrationData} matching the migration contract
     */
    public CourtMigrationData mapCourt(final Court court) {
        Objects.requireNonNull(court, "court must not be null");

        return new CourtMigrationData(
            toStringOrNull(court.getId()),
            court.getName(),
            court.getSlug(),
            Boolean.TRUE.equals(court.getDisplayed()),
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

    /**
     * Convert associated court postcodes into export DTOs.
     * @param court court entity including postcode data
     * @return list of postcode data or {@code null} when none
     */
    private List<CourtPostcodeData> mapCourtPostcodes(final Court court) {
        return mapToListOrNull(
            court.getCourtPostcodes(),
            courtPostcode -> new CourtPostcodeData(
                courtPostcode.getId(),
                courtPostcode.getPostcode()
            )
        );
    }

    /**
     * Convert core court code metadata into the migration DTO.
     * @param court court entity containing codes
     * @return court code data or {@code null} when all fields are empty
     */
    private CourtCodeData mapCourtCodes(final Court court) {
        String courtId = toStringOrNull(court.getId());
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
            court.getMagistrateCode(),
            court.getCourtCode(),
            court.getLocationCode(),
            court.getCciCode(),
            court.getNumber(),
            court.getGbs()
        );
    }

    /**
     * Translate service area court relationships into grouped export DTOs.
     * @param court court entity containing service area relationships
     * @return grouped service area data or {@code null} when none exist
     */
    private List<CourtServiceAreaData> mapCourtServiceAreas(final Court court) {
        List<ServiceAreaCourt> serviceAreaCourts = court.getServiceAreaCourts();
        if (isEmpty(serviceAreaCourts)) {
            return null;
        }

        Map<String, List<ServiceAreaCourt>> groupedByCatchment = serviceAreaCourts
            .stream()
            .collect(Collectors.groupingBy(
                ServiceAreaCourt::getCatchmentType,
                LinkedHashMap::new,
                Collectors.toList()
            ));

        List<CourtServiceAreaData> serviceAreas = groupedByCatchment.entrySet()
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
                    entry.getKey()
                );
            })
            .collect(Collectors.toList());
        return nullIfEmpty(serviceAreas);
    }

    /**
     * Translate court areas of law relationships into export DTOs.
     * @param court court entity
     * @return area of law data or {@code null} when no relationships exist
     */
    private CourtAreasOfLawData mapCourtAreasOfLaw(final Court court) {
        Integer courtId = court.getId();

        List<CourtAreaOfLaw> courtAreas = courtId == null
            ? List.of()
            : courtAreaOfLawRepository.getCourtAreaOfLawByCourtId(courtId);

        if (isEmpty(courtAreas)) {
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

        return new CourtAreasOfLawData(id, areaIds);
    }

    /**
     * Translate single point of entry relationships into export DTOs.
     * @param court court entity
     * @return SPOE data or {@code null} when no SPOE configuration exists
     */
    private CourtSinglePointOfEntryData mapCourtSinglePointsOfEntry(final Court court) {
        Integer courtId = court.getId();

        List<CourtAreaOfLawSpoe> courtAreas = courtId == null
            ? List.of()
            : courtAreaOfLawSpoeRepository.getAllByCourtId(courtId);

        if (isEmpty(courtAreas)) {
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

        return new CourtSinglePointOfEntryData(id, areaIds);
    }

    /**
     * Extract court fax numbers from contacts into export DTOs.
     * @param court court entity with contact associations
     * @return fax data or {@code null} when no fax contacts exist
     */
    private List<CourtFaxData> mapCourtFaxNumbers(final Court court) {
        return mapToListOrNull(court.getCourtContacts(), this::toFaxData);
    }

    /**
     * Build the hosted image URL for a court photo.
     * @param court court entity with potential image file name
     * @return photo DTO or {@code null} when no image is configured
     */
    private CourtPhotoData mapCourtPhoto(final Court court) {
        String imageFile = court.getImageFile();
        if (!StringUtils.hasText(imageFile)) {
            return null;
        }

        String base = StringUtils.hasText(imageBaseUrl) ? imageBaseUrl : "";
        String normalizedBase = StringUtils.trimTrailingCharacter(base, PATH_SEPARATOR_CHAR);
        String normalizedFile = StringUtils.trimLeadingCharacter(imageFile, PATH_SEPARATOR_CHAR);
        if (normalizedBase.isEmpty()) {
            return new CourtPhotoData(PATH_SEPARATOR_CHAR + normalizedFile);
        }
        return new CourtPhotoData(normalizedBase + PATH_SEPARATOR_CHAR + normalizedFile);
    }

    /**
     * Translate DX code associations into export DTOs.
     * @param court court entity with DX codes
     * @return list of DX code data or {@code null} when none exist
     */
    private List<CourtDxCodeData> mapCourtDxCodes(final Court court) {
        return mapToListOrNull(court.getCourtDxCodes(), this::toDxCodeData);
    }

    private CourtFaxData toFaxData(final CourtContact courtContact) {
        if (courtContact == null) {
            return null;
        }
        Contact contact = courtContact.getContact();
        if (contact == null || !contact.isFax()) {
            return null;
        }
        return new CourtFaxData(
            toStringOrNull(contact.getId()),
            contact.getNumber()
        );
    }

    private CourtDxCodeData toDxCodeData(final CourtDxCode courtDxCode) {
        if (courtDxCode == null) {
            return null;
        }
        uk.gov.hmcts.dts.fact.entity.DxCode dxCode = courtDxCode.getDxCode();
        if (dxCode == null) {
            return null;
        }
        return new CourtDxCodeData(
            toStringOrNull(dxCode.getId()),
            dxCode.getCode(),
            dxCode.getExplanation()
        );
    }

    private static boolean isEmpty(final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    private static <T> List<T> nullIfEmpty(final List<T> items) {
        return items == null || items.isEmpty() ? null : items;
    }

    private static <T, R> List<R> mapToListOrNull(final Collection<T> source, final Function<T, R> mapper) {
        if (isEmpty(source)) {
            return null;
        }

        List<R> mapped = source.stream()
            .map(mapper)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return mapped.isEmpty() ? null : mapped;
    }

    private static String toStringOrNull(final Integer value) {
        return value == null ? null : value.toString();
    }
}
