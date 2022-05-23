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
import uk.gov.hmcts.dts.fact.model.ServiceArea;
import uk.gov.hmcts.dts.fact.services.ServiceAreaService;

import static org.springframework.http.ResponseEntity.ok;

@RateLimiter(name = "default")
@RestController
@RequestMapping(
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class ServiceAreasController {

    private final ServiceAreaService serviceAreaService;

    @Autowired
    public ServiceAreasController(final ServiceAreaService serviceAreaService) {
        this.serviceAreaService = serviceAreaService;
    }

    @GetMapping(path = "/service-areas/{slug}")
    @ApiOperation("Return a service")
    public ResponseEntity<ServiceArea> getServiceArea(@PathVariable String slug) {
        return ok(serviceAreaService.getServiceArea(slug));
    }
}
