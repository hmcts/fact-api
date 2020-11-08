package uk.gov.hmcts.dts.fact.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.ServiceArea;
import uk.gov.hmcts.dts.fact.repositories.ServiceAreaRepository;

@Service
public class ServiceAreaService {

    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    public ServiceArea getServiceArea(final String slug) {
        return serviceAreaRepository
            .findBySlugIgnoreCase(slug)
            .map(ServiceArea::new)
            .orElseThrow(() -> new NotFoundException(slug));
    }
}
