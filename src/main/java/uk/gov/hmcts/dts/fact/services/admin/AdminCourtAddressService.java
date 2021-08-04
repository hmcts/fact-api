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
import uk.gov.hmcts.dts.fact.services.validation.ValidationService;
import uk.gov.hmcts.dts.fact.util.AddressType;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class AdminCourtAddressService {
    private final CourtRepository courtRepository;
    private final CourtAddressRepository courtAddressRepository;
    private final AdminAddressTypeService addressTypeService;
    private final AdminService adminService;
    private final MapitService mapitService;
    private final ValidationService validationService;

    @Autowired
    public AdminCourtAddressService(final CourtRepository courtRepository, final CourtAddressRepository courtAddressRepository, final AdminAddressTypeService addressTypeService, final AdminService adminService, final MapitService mapitService, final ValidationService validationService) {
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
    public List<CourtAddress> updateCourtAddressesAndCoordinates(final String slug, final List<CourtAddress> courtAddresses) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        final List<uk.gov.hmcts.dts.fact.entity.CourtAddress> newCourtAddressesEntity = constructCourtAddressesEntity(courtEntity, courtAddresses);
        courtAddressRepository.deleteAll(courtEntity.getAddresses());

        final List<CourtAddress> updatedCourtAddresses = courtAddressRepository.saveAll(newCourtAddressesEntity)
            .stream()
            .sorted(Comparator.comparingInt(a -> AddressType.isCourtAddress(a.getAddressType().getName()) ? 0 : 1))
            .map(CourtAddress::new)
            .collect(toList());

        // Update the in-person court with its primary postcode coordinates
        if (isInPersonCourt(slug) && !CollectionUtils.isEmpty(updatedCourtAddresses)) {
            final String primaryPostcode = updatedCourtAddresses.get(0).getPostcode();
            if (StringUtils.isNotBlank(primaryPostcode)) {
                updateCourtLatLonUsingPrimaryPostcode(slug, primaryPostcode);
            }
        }
        return updatedCourtAddresses;
    }

    public List<String> validateCourtAddressPostcodes(final String slug, final List<CourtAddress> courtAddresses) {
        if (!CollectionUtils.isEmpty(courtAddresses)) {
            final List<String> allPostcodes = getAllPostcodes(courtAddresses);

            if (!CollectionUtils.isEmpty(allPostcodes)) {
                return validationService.validateFullPostcodes(allPostcodes);
            }
        }
        return emptyList();
    }

    private List<String> getAllPostcodes(final List<CourtAddress> courtAddresses) {
        return courtAddresses.stream()
            .map(CourtAddress::getPostcode)
            .filter(StringUtils::isNotBlank)
            .collect(toList());
    }

    private boolean isInPersonCourt(final String slug) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        return courtEntity.getInPerson() == null || courtEntity.getInPerson().getIsInPerson();
    }

    private void updateCourtLatLonUsingPrimaryPostcode(final String slug, final String postcode) {
        final Optional<MapitData> mapitData = mapitService.getMapitData(postcode);
        if (mapitData.isPresent()) {
            adminService.updateCourtLatLon(slug, mapitData.get().getLat(), mapitData.get().getLon());
        }
    }

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
