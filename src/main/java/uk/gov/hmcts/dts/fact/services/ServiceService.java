package uk.gov.hmcts.dts.fact.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.repositories.ServiceRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ServiceService {

    @Autowired
    ServiceRepository serviceRepository;

    public List<uk.gov.hmcts.dts.fact.model.Service> getAllServices() {
        return serviceRepository
            .findAll()
            .stream()
            .map(uk.gov.hmcts.dts.fact.model.Service::new)
            .collect(toList());
    }

    public uk.gov.hmcts.dts.fact.model.Service getService(String serviceName) {
        return serviceRepository
            .findByNameIgnoreCase(serviceName)
            .map(uk.gov.hmcts.dts.fact.model.Service::new)
            .orElseThrow(() -> new NotFoundException(serviceName));
    }

    public List<uk.gov.hmcts.dts.fact.model.ServiceArea> getServiceAreas(String serviceName) {
        return serviceRepository
            .findByNameIgnoreCase(serviceName)
            .map(uk.gov.hmcts.dts.fact.model.Service::new)
            .map(uk.gov.hmcts.dts.fact.model.Service::getServiceAreas)
            .orElseThrow(() -> new NotFoundException(serviceName));
    }
}
