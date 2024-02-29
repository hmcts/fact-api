package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneralInfo;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtGeneralInfoService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@Slf4j
@RestController
@RequestMapping(
    path = "/admin/courts/{slug}/generalInfo",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtGeneralInfoController {
    private final AdminCourtGeneralInfoService adminService;
    private final AdminCourtLockService adminCourtLockService;

    @Autowired
    public AdminCourtGeneralInfoController(AdminCourtGeneralInfoService adminService,
                                           AdminCourtLockService adminCourtLockService) {
        this.adminService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    @GetMapping()
    @Operation(summary = "Retrieve court general information")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<CourtGeneralInfo> getCourtGeneralInfo(@PathVariable String slug) {
        return ok(adminService.getCourtGeneralInfoBySlug(slug));
    }

    @PutMapping()
    @Operation(summary = "Update court general information")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @ApiResponse(responseCode = "409", description = "Court already exists")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<CourtGeneralInfo> updateCourtGeneralInfo(@PathVariable String slug,
                                                                   @RequestBody CourtGeneralInfo generalInfo,
                                                                   Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminService.updateCourtGeneralInfo(slug, generalInfo));
    }
}
