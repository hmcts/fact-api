package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.Audit;
import uk.gov.hmcts.dts.fact.services.admin.AdminAuditService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/audit",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
@Slf4j
public class AdminAuditController {
    private final AdminAuditService adminAuditService;

    @Autowired
    public AdminAuditController(AdminAuditService adminAuditService) {
        this.adminAuditService = adminAuditService;
    }

    @GetMapping(params = {"page", "size"})
    @ApiOperation("Find all audits based on the provided parameters.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = Audit.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden")
    })
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<List<Audit>> getAudits(@RequestParam("page") int page,
                                                 @RequestParam("size") int size,
                                                 @RequestParam(value = "location", required = false) Optional<String> location,
                                                 @RequestParam(value = "email", required = false) Optional<String> email,
                                                 @RequestParam(value = "date-from", required = false)
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> dateFrom,
                                                 @RequestParam(value = "date-to", required = false)
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> dateTo) {

        // Return with an error if one or the other is set, but not both
        if (dateFrom.isPresent() && dateTo.isEmpty() || dateFrom.isEmpty() && dateTo.isPresent()) {
            log.error("Both date-from and date-to request parameters need to be set if present, and in the format "
                          + "of yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ok(adminAuditService.getAllAuditData(page, size, location, email, dateFrom, dateTo));
    }
}
