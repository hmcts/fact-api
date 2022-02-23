package uk.gov.hmcts.dts.fact.controllers.admin;

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

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(
    path = "/admin/serviceAreas",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminServiceAreaController {

    private final ServiceAreaService serviceAreaService;

    @Autowired
    public AdminServiceAreaController(final ServiceAreaService serviceAreaService) {
        this.serviceAreaService = serviceAreaService;
    }

    @GetMapping()
    @ApiOperation("Return all service areas")
    public ResponseEntity<List<ServiceArea>> getServiceArea() {
        return ok(serviceAreaService.getAllServiceAreas());
    }
}
