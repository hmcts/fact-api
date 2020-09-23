package uk.gov.hmcts.dts.fact.controllers.deprecated;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.services.CourtService;

import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping(
        path = "/courts",
        produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class CourtsController {

    @Autowired
    CourtService courtService;

    @Deprecated
    @GetMapping(path = "/{slug}.json")
    @ApiOperation("Find court details by name")
    public ResponseEntity<Court> findCourtByName(@PathVariable String slug) {
        return ok(courtService.getCourtBySlug(slug));
    }
}
