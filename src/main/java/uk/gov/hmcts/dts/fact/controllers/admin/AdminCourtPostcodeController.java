package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.exception.InvalidPostcodeException;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtPostcodeService;
import uk.gov.hmcts.dts.fact.services.validation.ValidationService;

import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtPostcodeController {
    private final AdminCourtPostcodeService adminService;
    private final ValidationService validationService;

    @Autowired
    public AdminCourtPostcodeController(AdminCourtPostcodeService adminService, ValidationService validationService) {
        this.adminService = adminService;
        this.validationService = validationService;
    }

    @GetMapping(path = "/{slug}/postcodes")
    @ApiOperation("Find court postcodes by slug")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<String>> getCourtPostcodes(@PathVariable String slug) {
        return ok(adminService.getCourtPostcodesBySlug(slug));
    }

    @PostMapping(path = "/{slug}/postcodes")
    @ApiOperation("Add new court postcodes")
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<String>> addCourtPostcodes(@PathVariable String slug, @RequestBody List<String> postcodes) {
        final List<String> postcodeCreated = adminService.addCourtPostcodes(slug, postcodes);
        return created(URI.create(StringUtils.EMPTY)).body(postcodeCreated);
        final List<String> invalidPostcodes = validationService.validatePostcodes(postcodes);
        if (CollectionUtils.isEmpty(invalidPostcodes)) {
            final List<String> postcodeCreated = adminService.addCourtPostcodes(slug, postcodes);
            return created(URI.create(StringUtils.EMPTY)).body(postcodeCreated);
        }
        throw new InvalidPostcodeException(invalidPostcodes);
    }

    @DeleteMapping(path = "/{slug}/postcodes")
    @ApiOperation("Delete court postcodes")
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity deleteCourtPostcodes(@PathVariable String slug, @RequestBody List<String> postcodes) {
        final List<String> invalidPostcodes = validationService.validatePostcodes(postcodes);
        if (CollectionUtils.isEmpty(invalidPostcodes)) {
            adminService.deleteCourtPostcodes(slug, postcodes);
            return ok().build();
        }
        throw new InvalidPostcodeException(invalidPostcodes);
    }
}
