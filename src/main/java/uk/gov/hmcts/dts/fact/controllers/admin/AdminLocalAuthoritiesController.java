package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.services.admin.AdminLocalAuthorityService;
import uk.gov.hmcts.dts.fact.services.validation.ValidationService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/localauthorities",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminLocalAuthoritiesController {
    private final AdminLocalAuthorityService adminLocalAuthorityService;
    private final ValidationService validationService;

    @Autowired
    public AdminLocalAuthoritiesController(AdminLocalAuthorityService localAuthorityService, ValidationService validationService) {
        this.adminLocalAuthorityService = localAuthorityService;
        this.validationService = validationService;
    }

    @GetMapping(path = "/all")
    @ApiOperation("Return all local authorities")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<LocalAuthority>> getAllLocalAuthorities() {
        return ok(adminLocalAuthorityService.getAllLocalAuthorities());
    }

    @PutMapping(path = "/{localAuthorityId}")
    @ApiOperation("Update local authority")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = LocalAuthority.class),
        @ApiResponse(code = 400, message = "Invalid Local Authority", response = String.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Local Authority not found")
    })
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<LocalAuthority> updateLocalAuthority(@PathVariable Integer localAuthorityId, @RequestBody LocalAuthority localAuthority) {
        if (localAuthority != null && validationService.validateLocalAuthority(localAuthority.getName())) {
            return ok(adminLocalAuthorityService.updateLocalAuthority(localAuthorityId, localAuthority));
        } else {
            String error = "Invalid Local Authority: " + ((localAuthority == null) ? "null" : localAuthority.getName());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }
    }
}
