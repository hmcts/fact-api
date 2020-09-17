package uk.gov.hmcts.dts.fact.controllers.deprecated;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    path = "/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class CourtsController {

    @Deprecated
    @GetMapping(path = "/{slug}.json")
    @ApiOperation("Find court details by name")
    public ResponseEntity<String> findCourtBySlug(

        @PathVariable String slug
    ) {
        return  new ResponseEntity<>("Not yet implemented", HttpStatus.NOT_IMPLEMENTED);
    }

}
