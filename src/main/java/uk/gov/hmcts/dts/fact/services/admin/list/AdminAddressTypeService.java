package uk.gov.hmcts.dts.fact.services.admin.list;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.model.admin.AddressType;
import uk.gov.hmcts.dts.fact.repositories.AddressTypeRepository;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class AdminAddressTypeService {
    private final AddressTypeRepository addressTypeRepository;

    public AdminAddressTypeService(AddressTypeRepository addressTypeRepository) {
        this.addressTypeRepository = addressTypeRepository;
    }

    public List<AddressType> getAllAddressTypes() {
        return addressTypeRepository.findAll()
            .stream()
            .map(AddressType::new)
            .collect(toList());
    }

    public Map<Integer, uk.gov.hmcts.dts.fact.entity.AddressType> getAddressTypeMap() {
        return addressTypeRepository.findAll()
            .stream()
            .collect(toMap(uk.gov.hmcts.dts.fact.entity.AddressType::getId, type -> type));
    }
}
