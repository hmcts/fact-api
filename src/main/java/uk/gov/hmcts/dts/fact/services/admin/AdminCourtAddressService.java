package uk.gov.hmcts.dts.fact.services.admin;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.dts.fact.entity.*;
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

import javax.persistence.Column;
import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
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

    public List<CourtAddress> getCourtAddressesBySlug(final String slug) {
        return courtRepository.findBySlug(slug)
            .map(c -> c.getAddresses()
                .stream()
                .sorted(Comparator.comparingInt(a -> AddressType.isCourtAddress(a.getAddressType().getName()) ? 0 : 1))
                .map(CourtAddress::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    @Transactional()
    public List<CourtAddress> updateCourtAddressesAndCoordinates(final String slug, final List<CourtAddress> courtAddresses) {
        // Update the in-person court with its primary postcode coordinates
        final List<String> postcodes = getAllPostcodesSortedByAddressType(courtAddresses);
        if (isInPersonCourt(slug) && !CollectionUtils.isEmpty(postcodes)) {
            final String primaryPostcode = postcodes.get(0);
            if (StringUtils.isNotBlank(primaryPostcode)) {
                updateCourtLatLonUsingPrimaryPostcode(slug, primaryPostcode);
            }
        }

        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        final List<uk.gov.hmcts.dts.fact.entity.CourtAddress> newCourtAddressesEntity = constructCourtAddressesEntity(
            courtEntity,
            courtAddresses
        );
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
        List<CourtAddress> updatedAddresses = updateResponseModel(updatedAddressesEntity
            .stream()
            .sorted(Comparator.comparingInt(a -> AddressType.isCourtAddress(a.getAddressType().getName()) ? 0 : 1))
            .map(CourtAddress::new)
            .collect(toList()), courtSecondaryAddressType);

        adminAuditService.saveAudit(
            AuditType.findByName("Update court addresses and coordinates"),
            courtEntity.getAddresses()
                .stream()
                .map(CourtAddress::new)
                .collect(toList()),
            updatedAddresses, slug
        );
        return updatedAddresses;
    }

    public List<String> validateCourtAddressPostcodes(final String slug, final List<CourtAddress> courtAddresses) {
        if (!CollectionUtils.isEmpty(courtAddresses)) {
            final List<String> allPostcodes = getAllPostcodesSortedByAddressType(courtAddresses);

            if (!CollectionUtils.isEmpty(allPostcodes)) {
                return validationService.validateFullPostcodes(allPostcodes);
            }
        }
        return emptyList();
    }

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

    private String getAddressTypeFromId(final Map<Integer, uk.gov.hmcts.dts.fact.entity.AddressType> map, final Integer addressTypeId) {
        if (!map.containsKey(addressTypeId)) {
            throw new IllegalArgumentException("Unknown address type ID: " + addressTypeId);
        }
        return map.get(addressTypeId).getName();
    }

    private boolean isInPersonCourt(final String slug) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        return courtEntity.isInPerson();
    }

    private void updateCourtLatLonUsingPrimaryPostcode(final String slug, final String postcode) {
        final Optional<MapitData> mapitData = mapitService.getMapitData(postcode);
        if (mapitData.isPresent()) {
            adminService.updateCourtLatLon(slug, mapitData.get().getLat(), mapitData.get().getLon());
        }
    }

    private List<uk.gov.hmcts.dts.fact.entity.CourtAddress> constructCourtAddressesEntity(final Court court, final List<CourtAddress> courtAddresses) {
        final Map<Integer, uk.gov.hmcts.dts.fact.entity.AddressType> addressTypeMap = addressTypeService.getAddressTypeMap();
        final Map<Integer, uk.gov.hmcts.dts.fact.entity.County> countyMap = countyService.getCountyMap();

        return courtAddresses.stream()
            .map(a -> new uk.gov.hmcts.dts.fact.entity.CourtAddress(
                court,
                addressTypeMap.get(a.getAddressTypeId()),
                a.getAddressLines(),
                a.getAddressLinesCy(),
                a.getTownName(),
                a.getTownNameCy(),
                countyMap.get(a.getCountyId()),
                a.getPostcode()
            ))
            .sorted(Comparator.comparingInt(a -> AddressType.isCourtAddress(a.getAddressType().getName()) ? 0 : 1))
            .collect(toList());
    }

    /**
     * Use the newly created addresses's id's to create and construct a
     * list of fields of law
     *
     * @param updatedCourtAddresses contains the id's of the newly created addresses
     * @param originalNewAddresses  contains the areas of law/court types required
     * @return a list of entities to be saved to the CourtSecondaryAddressTypeRepository
     */
    private List<CourtSecondaryAddressType> constructCourtSecondaryAddressTypes(final List<uk.gov.hmcts.dts.fact.entity.CourtAddress> updatedCourtAddresses,
                                                                                final List<CourtAddress> originalNewAddresses) {
        List<CourtSecondaryAddressType> courtSecondaryAddressTypeList = new ArrayList<>();

        // Where the addresses are the same
        // Get the id from the newly created address
        for (int i = 0; i < updatedCourtAddresses.size(); i++) {

            if (!Objects.isNull(originalNewAddresses.get(i).getCourtSecondaryAddressType().getAreaOfLawList())) {
                for (uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw areaOfLaw :
                    originalNewAddresses.get(i).getCourtSecondaryAddressType().getAreaOfLawList()) {
                    courtSecondaryAddressTypeList.add(new CourtSecondaryAddressType(
                        updatedCourtAddresses.get(i),
                        constructAreaOfLaw(areaOfLaw)
                    ));
                }
            }

            if (!Objects.isNull(originalNewAddresses.get(i).getCourtSecondaryAddressType().getCourtTypesList())) {
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

    private CourtType constructCourtType(uk.gov.hmcts.dts.fact.model.admin.CourtType courtType) {
        return new CourtType(
            courtType.getId(),
            courtType.getName()
        );
    }

    /**
     * Update the response model that is returned through the request with the information
     * provided
     * @param updatedAddresses contains addresses without entities
     * @param courtSecondaryAddressType contains entities that need to be added to addresses
     * @return An updated list of court addresses
     */
    private List<CourtAddress> updateResponseModel(List<CourtAddress> updatedAddresses,
                                                   List<CourtSecondaryAddressType> courtSecondaryAddressType) {
        List<CourtAddress> responseList = new ArrayList<>(updatedAddresses);
        for (int i = 0; i < responseList.size(); i++) {
            int finalI = i;
            List<uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw> areaOfLawList = courtSecondaryAddressType
                .stream()
                .filter(ca -> !Objects.isNull(ca.getAreaOfLaw()))
                .filter(ca -> ca.getAddress().getId() == responseList.get(finalI).getId())
                .map(a -> new uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw(a.getAreaOfLaw()))
                .collect(toList());
            List<uk.gov.hmcts.dts.fact.model.admin.CourtType> courtTypeList = courtSecondaryAddressType
                .stream()
                .filter(ca -> !Objects.isNull(ca.getCourtType()))
                .filter(ca -> ca.getAddress().getId() == responseList.get(finalI).getId())
                .map(a -> new uk.gov.hmcts.dts.fact.model.admin.CourtType(a.getCourtType()))
                .collect(toList());
            responseList.get(i).setCourtSecondaryAddressType(
                new uk.gov.hmcts.dts.fact.model.admin.CourtSecondaryAddressType(areaOfLawList, courtTypeList));
        }
        return responseList;
    }
}
