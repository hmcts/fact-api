package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtAddress;
import uk.gov.hmcts.dts.fact.repositories.CourtAddressRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminAddressTypeService;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtAddressService {
    private final CourtRepository courtRepository;
    private final CourtAddressRepository courtAddressRepository;
    private final AdminAddressTypeService addressTypeService;

    @Autowired
    public AdminCourtAddressService(final CourtRepository courtRepository, final CourtAddressRepository courtAddressRepository, final AdminAddressTypeService addressTypeService) {
        this.courtRepository = courtRepository;
        this.courtAddressRepository = courtAddressRepository;
        this.addressTypeService = addressTypeService;
    }

    public List<CourtAddress> getCourtAddressesBySlug(final String slug) {
        return courtRepository.findBySlug(slug)
            .map(c -> c.getAddresses()
                .stream()
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
            .map(CourtAddress::new)
            .collect(toList());
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
