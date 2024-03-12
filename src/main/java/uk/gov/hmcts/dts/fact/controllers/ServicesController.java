package uk.gov.hmcts.dts.fact.controllers;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.model.Service;
import uk.gov.hmcts.dts.fact.model.ServiceArea;
import uk.gov.hmcts.dts.fact.services.ServiceService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RateLimiter(name = "default")
@RestController
@RequestMapping(
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class ServicesController {

    private final ServiceService serviceService;

    /**
     * Construct a new ServicesController.
     * @param serviceService the 'service' service
     */
    @Autowired
    public ServicesController(final ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    /**
     * Return all services.
     * @return list of services
     */
    @GetMapping(path = "/services")
    @Operation(summary = "Return all services")
    public ResponseEntity<List<Service>> getAllServices() {
        return ok(serviceService.getAllServices());
    }

    /**
     * Return a service.
     * @param slug
     * @return service
     */
    @GetMapping(path = "/services/{slug}")
    @Operation(summary = "Return a service")
    public ResponseEntity<Service> getService(@PathVariable String slug) {
        return ok(serviceService.getService(slug));
    }

    /**
     * Return all service areas for a service.
     * @param serviceSlug
     * @return list of service areas
     */
    @GetMapping(path = "/services/{serviceSlug}/service-areas")
    @Operation(summary = "Return all service areas for a service")
    public ResponseEntity<List<ServiceArea>> getServiceAreas(@PathVariable String serviceSlug) {
        return ok(serviceService.getServiceAreas(serviceSlug));
    }
}
