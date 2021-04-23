package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.model.admin.Email;
import uk.gov.hmcts.dts.fact.model.admin.EmailType;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtEmailService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtEmailController {
    private final AdminCourtEmailService adminService;

    @Autowired
    public AdminCourtEmailController(AdminCourtEmailService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(path = "/{slug}/emails")
    @ApiOperation("Find email addresses by slug")
//    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<Email>> getCourtOpeningTimes(@PathVariable String slug) {
        return ok(adminService.getCourtEmailsBySlug(slug));
    }

//    @PutMapping(path = "/{slug}/emails")
//    @ApiOperation("Update email addresses for provided court")
//    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
//    public ResponseEntity<List<OpeningTime>> updateCourtOpeningTimes(@PathVariable String slug, @RequestBody List<OpeningTime> openingTimes) {
//        return ok(Collections.emptyList());
//    }

    @GetMapping(path = "/emails")
    @ApiOperation("Retrieve all email details for provided court")
//    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<EmailType>> getAllCourtOpeningTypes() {
        return ok(adminService.getAllEmailTypes());
    }
}
