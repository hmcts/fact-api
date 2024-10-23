package uk.gov.hmcts.dts.fact.services.admin;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.County;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtSecondaryAddressType;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.admin.CourtAddress;
import uk.gov.hmcts.dts.fact.repositories.CourtAddressRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtSecondaryAddressTypeRepository;
import uk.gov.hmcts.dts.fact.services.MapitService;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminAddressTypeService;
import uk.gov.hmcts.dts.fact.services.validation.ValidationService;
import uk.gov.hmcts.dts.fact.util.AddressType;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * Service for admin court address data.
 */
@Service
@SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.ExcessiveParameterList"})
public class AdminCourtAddressService {
    private final CourtRepository courtRepository;
    private final CourtAddressRepository courtAddressRepository;
    private final CourtSecondaryAddressTypeRepository courtSecondaryAddressTypeRepository;
    private final AdminAddressTypeService addressTypeService;
    private final AdminCountyService countyService;
    private final AdminService adminService;
    private final MapitService mapitService;
    private final ValidationService validationService;
    private final AdminAuditService adminAuditService;

    /**
     * Constructor for the AdminCourtAddressService.
     * @param courtRepository The repository for court
     * @param courtAddressRepository The repository for court address
     * @param courtSecondaryAddressTypeRepository The repository for court secondary address type
     * @param addressTypeService The service for address type
     * @param countyService The service for county
     * @param adminService The service for admin
     * @param mapitService The service for mapit
     * @param validationService The service for validation
     * @param adminAuditService The service for admin audit
     */
    @Autowired
    public AdminCourtAddressService(final CourtRepository courtRepository,
                                    final CourtAddressRepository courtAddressRepository,
                                    final CourtSecondaryAddressTypeRepository courtSecondaryAddressTypeRepository,
                                    final AdminAddressTypeService addressTypeService,
                                    final AdminCountyService countyService,
                                    final AdminService adminService,
                                    final MapitService mapitService,
                                    final ValidationService validationService,
                                    final AdminAuditService adminAuditService) {
        this.courtRepository = courtRepository;
        this.courtAddressRepository = courtAddressRepository;
        this.courtSecondaryAddressTypeRepository = courtSecondaryAddressTypeRepository;
        this.addressTypeService = addressTypeService;
        this.countyService = countyService;
        this.adminService = adminService;
        this.mapitService = mapitService;
        this.validationService = validationService;
        this.adminAuditService = adminAuditService;
    }

    /**
     * Get all court addresses by slug.
     * @param slug The slug
     * @return A list of court addresses
     */
    public List<CourtAddress> getCourtAddressesBySlug(final String slug) {
        return courtRepository.findBySlug(slug)
            .map(c -> c.getAddresses()
                .stream()
                .map(CourtAddress::new)
                .sorted(Comparator.comparingInt(CourtAddress::getSortOrder))
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    /**
     * Update court addresses and coordinates.
     * @param slug The slug
     * @param courtAddresses The court addresses
     * @return A list of court addresses
     */
    @Transactional()
    public List<CourtAddress> updateCourtAddressesAndCoordinates(final String slug, final List<CourtAddress> courtAddresses) {
        // Update the in-person court with its primary postcode coordinates and region
        final List<String> postcodes = getAllPostcodesSortedByAddressType(courtAddresses);
        if (isInPersonCourt(slug) && !CollectionUtils.isEmpty(postcodes)) {
            final String primaryPostcode = postcodes.get(0);
            if (StringUtils.isNotBlank(primaryPostcode)) {
                updateCourtLatLonAndRegionUsingPrimaryPostcode(
                    slug,
                    primaryPostcode);
            }
        }

        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        final List<uk.gov.hmcts.dts.fact.entity.CourtAddress> newCourtAddressesEntity = constructCourtAddressesEntity(
            courtEntity,
            courtAddresses
        );

        deleteExistingSecondaryAddressTypes(slug);
        courtAddressRepository.deleteAll(courtEntity.getAddresses());

        List<uk.gov.hmcts.dts.fact.entity.CourtAddress> updatedAddressesEntity =
            courtAddressRepository.saveAll(newCourtAddressesEntity);

        // After the court addresses have been saved, use the new address Id to update the
        // secondary addresses to include the areas of law and court types they govern
        final List<CourtSecondaryAddressType> courtSecondaryAddressType = courtSecondaryAddressTypeRepository.saveAll(
            constructCourtSecondaryAddressTypes(
                updatedAddressesEntity,
                courtAddresses
            ));

        // Update the responding model to include the new secondary address types
        // Note: the id's will not be created until the transaction is commited, so calling
        // the get addresses by slug method will not work for this purpose
        List<CourtAddress> updatedAddresses = updateResponseModel(updatedAddressesEntity
                                                                      .stream()
                                                                      .map(CourtAddress::new)
                                                                      .sorted(Comparator.comparingInt(CourtAddress::getSortOrder))
                                                                      .collect(toList()), courtSecondaryAddressType);

        adminAuditService.saveAudit(
            AuditType.findByName("Update court addresses and coordinates"),
            courtAddresses,
            updatedAddresses, slug
        );
        return updatedAddresses;
    }

    /**
     * Validate court address postcodes.
     * @param courtAddresses The court addresses
     * @return A list of postcodes
     */
    public List<String> validateCourtAddressPostcodes(final List<CourtAddress> courtAddresses) {
        if (!CollectionUtils.isEmpty(courtAddresses)) {
            final List<String> allPostcodes = getAllPostcodesSortedByAddressType(courtAddresses);

            if (!CollectionUtils.isEmpty(allPostcodes)) {
                return validationService.validateFullPostcodes(allPostcodes);
            }
        }
        return emptyList();
    }

    /**
     * Get all postcodes sorted by address type.
     * @param courtAddresses The court addresses
     * @return A list of postcodes
     */
    private List<String> getAllPostcodesSortedByAddressType(final List<CourtAddress> courtAddresses) {
        final Map<Integer, uk.gov.hmcts.dts.fact.entity.AddressType> addressTypeMap = addressTypeService.getAddressTypeMap();
        return courtAddresses.stream()
            .sorted(Comparator.comparingInt(a -> AddressType.isCourtAddress(getAddressTypeFromId(
                addressTypeMap,
                a.getAddressTypeId()
            )) ? 0 : 1))
            .map(CourtAddress::getPostcode)
            .filter(StringUtils::isNotBlank)
            .collect(toList());
    }

    /**
     * Get address type from id.
     * @param map The map
     * @param addressTypeId The address type id
     * @return The address type
     */
    private String getAddressTypeFromId(final Map<Integer, uk.gov.hmcts.dts.fact.entity.AddressType> map, final Integer addressTypeId) {
        if (!map.containsKey(addressTypeId)) {
            throw new IllegalArgumentException("Unknown address type ID: " + addressTypeId);
        }
        return map.get(addressTypeId).getName();
    }

    /**
     * Check if the court is an in-person court.
     * @param slug The slug
     * @return A boolean indicating if the court is an in-person court
     */
    private boolean isInPersonCourt(final String slug) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        return courtEntity.isInPerson();
    }

    /**
     * Update court lat lon and region using primary postcode.
     * @param slug The slug
     * @param postcode The postcode
     */
    private void updateCourtLatLonAndRegionUsingPrimaryPostcode(final String slug, final String postcode) {
        final Optional<MapitData> mapitData = mapitService.getMapitData(postcode);
        if (mapitData.isPresent()) {
            adminService.updateCourtLatLon(slug, mapitData.get().getLat(), mapitData.get().getLon());
            adminService.updateCourtRegion(slug, mapitData.get().getRegionFromMapitData());
        }
    }

    /**
     * Construct court addresses entity.
     * @param court The court
     * @param courtAddresses The court addresses
     * @return A list of court addresses
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<uk.gov.hmcts.dts.fact.entity.CourtAddress> constructCourtAddressesEntity(final Court court, final List<CourtAddress> courtAddresses) {
        final Map<Integer, uk.gov.hmcts.dts.fact.entity.AddressType> addressTypeMap = addressTypeService.getAddressTypeMap();
        final Map<Integer, County> countyMap = countyService.getCountyMap();

        final List<uk.gov.hmcts.dts.fact.entity.CourtAddress> courtAddressArrayList = new ArrayList<>();
        for (int i = 0; i < courtAddresses.size(); i++) {
            courtAddressArrayList.add(new uk.gov.hmcts.dts.fact.entity.CourtAddress(court,
                  addressTypeMap.get(courtAddresses.get(i).getAddressTypeId()),
                  courtAddresses.get(i).getAddressLines(),
                  courtAddresses.get(i).getAddressLinesCy(),
                  courtAddresses.get(i).getTownName(),
                  courtAddresses.get(i).getTownNameCy(),
                  countyMap.get(courtAddresses.get(i).getCountyId()),
                  courtAddresses.get(i).getPostcode(),
                  i,
                  courtAddresses.get(i).getEpimId())
            );
        }
        return courtAddressArrayList;
    }

    /**
     * Use the newly created addresses's id's to create and construct a
     * list of fields of law.
     *
     * @param updatedCourtAddresses contains the id's of the newly created addresses
     * @param originalNewAddresses  contains the areas of law/court types required
     * @return a list of entities to be saved to the CourtSecondaryAddressTypeRepository
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<CourtSecondaryAddressType> constructCourtSecondaryAddressTypes(final List<uk.gov.hmcts.dts.fact.entity.CourtAddress> updatedCourtAddresses,
                                                                                final List<CourtAddress> originalNewAddresses) {
        List<CourtSecondaryAddressType> courtSecondaryAddressTypeList = new ArrayList<>();

        // Where the addresses are the same
        // Get the id from the newly created address
        for (int i = 0; i < updatedCourtAddresses.size(); i++) {

            if (!Objects.isNull(originalNewAddresses.get(i).getCourtSecondaryAddressType())
                && !Objects.isNull(originalNewAddresses.get(i).getCourtSecondaryAddressType().getAreaOfLawList())) {
                for (uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw areaOfLaw :
                    originalNewAddresses.get(i).getCourtSecondaryAddressType().getAreaOfLawList()) {
                    courtSecondaryAddressTypeList.add(new CourtSecondaryAddressType(
                        updatedCourtAddresses.get(i),
                        constructAreaOfLaw(areaOfLaw)
                    ));
                }
            }

            if (!Objects.isNull(originalNewAddresses.get(i).getCourtSecondaryAddressType())
                && !Objects.isNull(originalNewAddresses.get(i).getCourtSecondaryAddressType().getCourtTypesList())) {
                for (uk.gov.hmcts.dts.fact.model.admin.CourtType courtType :
                    originalNewAddresses.get(i).getCourtSecondaryAddressType().getCourtTypesList()) {
                    courtSecondaryAddressTypeList.add(new CourtSecondaryAddressType(
                        updatedCourtAddresses.get(i),
                        constructCourtType(courtType)
                    ));
                }
            }
        }
        return courtSecondaryAddressTypeList;
    }

    /**
     * Construct area of law.
     * @param areaOfLaw The area of law
     * @return The area of law
     */
    private AreaOfLaw constructAreaOfLaw(uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw areaOfLaw) {
        return new AreaOfLaw(areaOfLaw.getId(),
                             areaOfLaw.getName(),
                             areaOfLaw.getExternalLink(),
                             "", // Cy external link is not present in model
                             areaOfLaw.getExternalLinkDescription(),
                             areaOfLaw.getExternalLinkDescriptionCy(),
                             areaOfLaw.getAlternativeName(),
                             areaOfLaw.getAlternativeNameCy(),
                             areaOfLaw.getDisplayName(), areaOfLaw.getDisplayNameCy(),
                             areaOfLaw.getDisplayExternalLink()
        );
    }

    /**
     * Construct court type.
     * @param courtType The court type
     * @return The court type
     */
    private CourtType constructCourtType(uk.gov.hmcts.dts.fact.model.admin.CourtType courtType) {
        return new CourtType(
            courtType.getId(),
            courtType.getName(),
            courtType.getSearch()
        );
    }

    /**
     * Update the response model that is returned through the request with the information
     * provided.
     *
     * @param updatedAddresses          contains addresses without entities
     * @param courtSecondaryAddressType contains entities that need to be added to addresses
     * @return An updated list of court addresses
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<CourtAddress> updateResponseModel(List<CourtAddress> updatedAddresses,
                                                   List<CourtSecondaryAddressType> courtSecondaryAddressType) {
        List<CourtAddress> responseList = new ArrayList<>(updatedAddresses);
        for (int i = 0; i < responseList.size(); i++) {
            int finalI = i;
            List<uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw> areaOfLawList = courtSecondaryAddressType
                .stream()
                .filter(ca -> !Objects.isNull(ca.getAreaOfLaw()))
                .filter(ca -> ca.getAddress().getId().equals(responseList.get(finalI).getId()))
                .map(a -> new uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw(a.getAreaOfLaw()))
                .collect(toList());
            List<uk.gov.hmcts.dts.fact.model.admin.CourtType> courtTypeList = courtSecondaryAddressType
                .stream()
                .filter(ca -> !Objects.isNull(ca.getCourtType()))
                .filter(ca -> ca.getAddress().getId().equals(responseList.get(finalI).getId()))
                .map(a -> new uk.gov.hmcts.dts.fact.model.admin.CourtType(a.getCourtType()))
                .collect(toList());
            responseList.get(i).setCourtSecondaryAddressType(
                new uk.gov.hmcts.dts.fact.model.admin.CourtSecondaryAddressType(areaOfLawList, courtTypeList));
        }
        return responseList;
    }

    /**
     * Delete existing secondary address types.
     * @param slug The slug
     */
    private void deleteExistingSecondaryAddressTypes(String slug) {
        List<Integer> secondaryTypesToRemove = getCourtAddressesBySlug(slug)
            .stream()
            .map(CourtAddress::getId)
            .collect(toList());
        courtSecondaryAddressTypeRepository.deleteAll(courtSecondaryAddressTypeRepository
                                                          .findAllByAddressIdIn(secondaryTypesToRemove)
                                                          .stream()
                                                          .distinct()
                                                          .collect(toList()));
        courtSecondaryAddressTypeRepository.deleteAllByAddressIdIn(getCourtAddressesBySlug(slug)
                                                                       .stream()
                                                                       .map(CourtAddress::getId)
                                                                       .collect(toList()));
    }

    /**
    * Validate court address epim ids.
    * @param courtAddresses The court addresses
    * @return epim id if invalid else null
     */
    public String validateCourtAddressEpimId(List<CourtAddress> courtAddresses) {
        if (!CollectionUtils.isEmpty(courtAddresses)) {
            return validationService.validateEpimIds(courtAddresses);
        }
        return null;
    }
}
