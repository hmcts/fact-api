package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.Audit;
import uk.gov.hmcts.dts.fact.services.admin.AdminAuditService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/audit",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminAuditController {
    private final AdminAuditService adminAuditService;

    @Autowired
    public AdminAuditController(AdminAuditService adminAuditService) {
        this.adminAuditService = adminAuditService;
    }

    @GetMapping(params = { "page", "size" })
    @ApiOperation("Find all audits based on the provided page and size.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = Audit.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden")
    })
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<List<Audit>> getAudits(@RequestParam("page") int page,
                                                 @RequestParam("size") int size) {
        return ok(adminAuditService.getAllAuditData(page, size));
    }
}
