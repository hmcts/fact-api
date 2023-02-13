package uk.gov.hmcts.dts.fact.controllers;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.annotations.ApiOperation;
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

    @Autowired
    public ServicesController(final ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping(path = "/services")
    @ApiOperation("Return all services")
    public ResponseEntity<List<Service>> getAllServices() {
        return ok(serviceService.getAllServices());
    }

    @GetMapping(path = "/services/{slug}")
    @ApiOperation("Return a service")
    public ResponseEntity<Service> getService(@PathVariable String slug) {
        return ok(serviceService.getService(slug));
    }

    @GetMapping(path = "/services/{serviceSlug}/service-areas")
    @ApiOperation("Return all service areas for a service")
    public ResponseEntity<List<ServiceArea>> getServiceAreas(@PathVariable String serviceSlug) {
        return ok(serviceService.getServiceAreas(serviceSlug));
    }
}
