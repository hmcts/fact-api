package uk.gov.hmcts.dts.fact.controllers.admin.list;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.ContactType;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminContactTypeService;

import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

/**
 * Controller for retrieving contact types.
 */
@RateLimiter(name = "relaxed")
@RestController
@RequestMapping(
    path = "/admin/contactTypes",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class AdminContactTypeController {

    private final AdminContactTypeService adminContactTypeService;

    /**
     * Constructor for the contact type controller.
     * @param adminContactTypeService The service to retrieve contact types
     */
    @Autowired
    public AdminContactTypeController(AdminContactTypeService adminContactTypeService) {
        this.adminContactTypeService = adminContactTypeService;
    }

    /**
     * Retrieve all contact types.
     * @return List of contact types
     */
    @GetMapping()
    @Operation(summary = "Return all contact types")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<ContactType>> getAllContactTypes() {
        return ok(adminContactTypeService.getAllContactTypes());
    }

    /**
     * Retrieve a contact type by id.
     * @param id The id of the contact type
     * @return The contact type
     */
    @GetMapping(path = "/{id}")
    @Operation(summary = "Get contact type")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Contact type not found")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<ContactType> getContactType(@PathVariable Integer id) {
        return ok(adminContactTypeService.getContactType(id));
    }

    /**
     * Create a contact type.
     * @param contactType The contact type to create
     * @return The created contact type
     */
    @PostMapping()
    @Operation(summary = "Create contact type")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "409", description = "Contact type already exists")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<ContactType> createContactType(@RequestBody ContactType contactType) {
        return created(URI.create(StringUtils.EMPTY)).body(adminContactTypeService.createContactType(contactType));
    }

    /**
     * Update a contact type.
     * @param contactType The contact type to update
     * @return The updated contact type
     */
    @PutMapping()
    @Operation(summary = "Update contact type")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Contact type not found")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<ContactType> updateContactType(@RequestBody ContactType contactType) {
        return ok(adminContactTypeService.updateContactType(contactType));
    }

    /**
     * Delete a contact type.
     * @param contactTypeId The id of the contact type to delete
     * @return The id of the deleted contact type
     */
    @DeleteMapping("/{contactTypeId}")
    @Operation(summary = "Delete contact type")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Contact type not found")
    @ApiResponse(responseCode = "409", description = "Contact type in use")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<Integer> deleteContactType(@PathVariable Integer contactTypeId) {
        adminContactTypeService.deleteContactType(contactTypeId);
        return ok().body(contactTypeId);
    }

}


