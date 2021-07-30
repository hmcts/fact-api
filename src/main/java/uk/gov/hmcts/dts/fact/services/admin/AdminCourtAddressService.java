package uk.gov.hmcts.dts.fact.services.admin;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.admin.CourtAddress;
import uk.gov.hmcts.dts.fact.repositories.CourtAddressRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.services.MapitService;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminAddressTypeService;
import uk.gov.hmcts.dts.fact.services.validation.PostcodeValidator;
import uk.gov.hmcts.dts.fact.services.validation.ValidationService;
import uk.gov.hmcts.dts.fact.util.AddressType;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtAddressService {
    private final CourtRepository courtRepository;
    private final CourtAddressRepository courtAddressRepository;
    private final AdminAddressTypeService addressTypeService;
    private final AdminService adminService;
    private final MapitService mapitService;
    private final ValidationService validationService;

    @Autowired
    public AdminCourtAddressService(final CourtRepository courtRepository, final CourtAddressRepository courtAddressRepository, final AdminAddressTypeService addressTypeService, AdminService adminService, MapitService mapitService, ValidationService validationService) {
        this.courtRepository = courtRepository;
        this.courtAddressRepository = courtAddressRepository;
        this.addressTypeService = addressTypeService;
        this.adminService = adminService;
        this.mapitService = mapitService;
        this.validationService = validationService;
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
    public List<CourtAddress> updateCourtAddresses(final String slug, final List<CourtAddress> courtAddresses) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        final List<uk.gov.hmcts.dts.fact.entity.CourtAddress> newCourtAddressesEntity = constructCourtAddressesEntity(courtEntity, courtAddresses);
        courtAddressRepository.deleteAll(courtEntity.getAddresses());

        return courtAddressRepository.saveAll(newCourtAddressesEntity)
            .stream()
            .sorted(Comparator.comparingInt(a -> AddressType.isCourtAddress(a.getAddressType().getName()) ? 0 : 1))
            .map(CourtAddress::new)
            .collect(toList());
    }

    public List<String> validateCourtAddressPostcodes(final String slug, final List<CourtAddress> courtAddresses) {
        final List<String> invalidPostcodes = new ArrayList<>();

        if (!CollectionUtils.isEmpty(courtAddresses)) {
            final List<String> allPostcodes = getAllPostcodes(courtAddresses);
            final List<String> visitUsPostcodes = getVisitUsPostcodes(courtAddresses);

            if (!CollectionUtils.isEmpty(allPostcodes)) {
                invalidPostcodes.addAll(validationService.validateFullPostcodes(allPostcodes));
            }

            if (invalidPostcodes.isEmpty() && !CollectionUtils.isEmpty(visitUsPostcodes)) {
                setCourtLatLonUsingPrimaryPostcode(slug, visitUsPostcodes.get(0));
            }
        }
        return invalidPostcodes;
    }

    private List<String> getAllPostcodes(final List<CourtAddress> courtAddresses) {
        return courtAddresses.stream()
            .map(CourtAddress::getPostcode)
            .filter(StringUtils::isNotBlank)
            .collect(toList());
    }

    private List<String> getVisitUsPostcodes(final List<CourtAddress> courtAddresses) {
        final Map<Integer, uk.gov.hmcts.dts.fact.entity.AddressType> addressTypeMap = addressTypeService.getAddressTypeMap();
        return courtAddresses.stream()
            .filter(a -> AddressType.isCourtAddress(getAddressTypeFromId(addressTypeMap, a.getAddressTypeId())))
            .map(CourtAddress::getPostcode)
            .collect(toList());
    }

    private String getAddressTypeFromId(final Map<Integer, uk.gov.hmcts.dts.fact.entity.AddressType> map, final Integer addressTypeId) {
        if (!map.containsKey(addressTypeId)) {
            throw new IllegalArgumentException("Unknown address type ID: " + addressTypeId);
        }
        return map.get(addressTypeId).getName();
    }

    private void setCourtLatLonUsingPrimaryPostcode(final String slug, final String postcode) {
        if (postcode.isBlank()) {
            // Remove the coordinates if the primary postcode is blank
            adminService.updateCourtLatLon(slug, null, null);
        } else {
            final Optional<MapitData> mapitData = mapitService.getMapitData(postcode);
            if (mapitData.isPresent()) {
                adminService.updateCourtLatLon(slug, mapitData.get().getLat(), mapitData.get().getLon());
            }
        }
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private List<uk.gov.hmcts.dts.fact.entity.CourtAddress> constructCourtAddressesEntity(final Court court, final List<CourtAddress> courtAddresses) {
        final Map<Integer, uk.gov.hmcts.dts.fact.entity.AddressType> addressTypeMap = addressTypeService.getAddressTypeMap();

        return courtAddresses.stream()
            .map(a -> new uk.gov.hmcts.dts.fact.entity.CourtAddress(court,
                                                                    addressTypeMap.get(a.getAddressTypeId()),
                                                                    a.getAddressLines(),
                                                                    a.getAddressLinesCy(),
                                                                    a.getTownName(),
                                                                    a.getTownNameCy(),
                                                                    a.getPostcode()))
            .collect(toList());
    }
}
