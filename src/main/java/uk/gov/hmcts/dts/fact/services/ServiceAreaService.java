package uk.gov.hmcts.dts.fact.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.ServiceArea;
import uk.gov.hmcts.dts.fact.repositories.ServiceAreaRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Service to get service areas.
 */
@Service
public class ServiceAreaService {

    private final ServiceAreaRepository serviceAreaRepository;

    /**
     * Constructor for the ServiceAreaService.
     *
     * @param serviceAreaRepository the repository to get service areas from
     */
    @Autowired
    public ServiceAreaService(final ServiceAreaRepository serviceAreaRepository) {
        this.serviceAreaRepository = serviceAreaRepository;
    }

    /**
     * Get a service area by slug.
     *
     * @param slug the slug of the service area
     * @return the service area
     */
    public ServiceArea getServiceArea(final String slug) {
        return serviceAreaRepository
            .findBySlugIgnoreCase(slug)
            .map(ServiceArea::new)
            .orElseThrow(() -> new NotFoundException(slug));
    }

    /**
     * Get all service areas.
     *
     * @return the list of service areas
     */
    public List<ServiceArea> getAllServiceAreas() {
        return serviceAreaRepository
            .findAll()
            .stream()
            .map(ServiceArea::new).collect(toList());
    }
}
