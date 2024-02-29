package uk.gov.hmcts.dts.fact.controllers.admin.list;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.exception.IllegalListItemException;
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminLocalAuthorityService;
import uk.gov.hmcts.dts.fact.services.validation.ValidationService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_VIEWER;

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
    @Operation(summary = "Return all local authorities")
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    public ResponseEntity<List<LocalAuthority>> getAllLocalAuthorities() {
        return ok(adminLocalAuthorityService.getAllLocalAuthorities());
    }

    @PutMapping(path = "/{localAuthorityId}")
    @Operation(summary = "Update local authority")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "400", description = "Invalid Local Authority")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Local Authority not found")
    @ApiResponse(responseCode = "409", description = "Local Authority already exists")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<LocalAuthority> updateLocalAuthority(@PathVariable Integer localAuthorityId, @RequestBody String name) {
        if (name == null || !validationService.validateLocalAuthority(name)) {
            throw new IllegalListItemException("Invalid Local Authority: " + ((name == null) ? "null" : name));
        }

        return ok(adminLocalAuthorityService.updateLocalAuthority(localAuthorityId, name));
    }
}
