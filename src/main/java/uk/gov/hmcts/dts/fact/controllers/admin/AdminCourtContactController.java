package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.Contact;
import uk.gov.hmcts.dts.fact.model.admin.ContactType;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtContactService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtContactController {
    private final AdminCourtContactService adminService;

    @Autowired
    public AdminCourtContactController(AdminCourtContactService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(path = "/{slug}/contacts")
    @ApiOperation("Find court contacts by slug")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<Contact>> getCourtContacts(@PathVariable String slug) {
        return ok(adminService.getCourtContactsBySlug(slug));
    }

    @PutMapping(path = "/{slug}/contacts")
    @ApiOperation("Update court contacts")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<Contact>> updateCourtContacts(@PathVariable String slug, @RequestBody List<Contact> contacts) {
        return ok(adminService.updateCourtContacts(slug, contacts));
    }

    @GetMapping(path = "/contactTypes")
    @ApiOperation("Retrieve all court contact types")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<ContactType>> getAllCourtContactTypes() {
        return ok(adminService.getAllCourtContactTypes());
    }
}
