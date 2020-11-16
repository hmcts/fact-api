package uk.gov.hmcts.dts.fact.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.repositories.ServiceRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;

    @Autowired
    public ServiceService(final ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<uk.gov.hmcts.dts.fact.model.Service> getAllServices() {
        return serviceRepository
            .findAll()
            .stream()
            .map(uk.gov.hmcts.dts.fact.model.Service::new)
            .collect(toList());
    }

    public uk.gov.hmcts.dts.fact.model.Service getService(String slug) {
        return serviceRepository
            .findBySlugIgnoreCase(slug)
            .map(uk.gov.hmcts.dts.fact.model.Service::new)
            .orElseThrow(() -> new NotFoundException(slug));
    }

    public List<uk.gov.hmcts.dts.fact.model.ServiceArea> getServiceAreas(String serviceSlug) {
        return serviceRepository
            .findBySlugIgnoreCase(serviceSlug)
            .map(uk.gov.hmcts.dts.fact.model.Service::new)
            .map(uk.gov.hmcts.dts.fact.model.Service::getServiceAreas)
            .orElseThrow(() -> new NotFoundException(serviceSlug));
    }
}
