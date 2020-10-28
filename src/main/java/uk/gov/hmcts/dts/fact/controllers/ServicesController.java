package uk.gov.hmcts.dts.fact.controllers;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.Service;
import uk.gov.hmcts.dts.fact.model.ServiceArea;
import uk.gov.hmcts.dts.fact.services.ServiceService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class ServicesController {

    @Autowired
    ServiceService serviceService;

    @GetMapping(path = "/services")
    @ApiOperation("Return all services")
    public ResponseEntity<List<Service>> getAllServices() {
        return ok(serviceService.getAllServices());
    }

    @GetMapping(path = "/services/{serviceName}")
    @ApiOperation("Return a service")
    public ResponseEntity<Service> getServices(@PathVariable String serviceName) {
        return ok(serviceService.getService(serviceName));
    }

    @GetMapping(path = "/services/{serviceName}/service-areas")
    @ApiOperation("Return all service areas for a service")
    public ResponseEntity<List<ServiceArea>> getServiceAreas(@PathVariable String serviceName) {
        return ok(serviceService.getServiceAreas(serviceName));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String slugNotFoundHandler(NotFoundException ex) {
        return ex.getMessage();
    }
}
