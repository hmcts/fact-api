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
import uk.gov.hmcts.dts.fact.model.ServiceArea;
import uk.gov.hmcts.dts.fact.services.ServiceAreaService;

import static org.springframework.http.ResponseEntity.ok;

/**
 * Controller for retrieving service areas.
 */
@RateLimiter(name = "default")
@RestController
@RequestMapping(
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class ServiceAreasController {

    private final ServiceAreaService serviceAreaService;

    /**
     * Constructor for ServiceAreasController.
     * @param serviceAreaService - the service area service
     */
    @Autowired
    public ServiceAreasController(final ServiceAreaService serviceAreaService) {
        this.serviceAreaService = serviceAreaService;
    }

    /**
     * Get service area from court slug.
     * @param slug - court slug
     * @return service area from court slug
     */
    @GetMapping(path = "/service-areas/{slug}")
    @Operation(summary = "Return a service")
    public ResponseEntity<ServiceArea> getServiceArea(@PathVariable String slug) {
        return ok(serviceAreaService.getServiceArea(slug));
    }
}
