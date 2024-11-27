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
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminAreasOfLawService;

import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

/**
 * Controller for retrieving areas of law.
 */
@RateLimiter(name = "relaxed")
@RestController
@RequestMapping(
    path = "/admin/areasOfLaw",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class AdminAreasOfLawController {
    private final AdminAreasOfLawService adminAreasOfLawService;

    /**
     * Constructor for the areas of law controller.
     * @param adminAreasOfLawService The service to retrieve areas of law
     */
    @Autowired
    public AdminAreasOfLawController(AdminAreasOfLawService adminAreasOfLawService) {
        this.adminAreasOfLawService = adminAreasOfLawService;
    }

    /**
     * Retrieve all areas of law.
     * @return List of areas of law
     */
    @GetMapping()
    @Operation(summary = "Return all areas of law")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<AreaOfLaw>> getAllAreasOfLaw() {
        return ok(adminAreasOfLawService.getAllAreasOfLaw());
    }

    /**
     * Retrieve an area of law by id.
     * @param id The id of the area of law
     * @return The area of law
     */
    @GetMapping(path = "/{id}")
    @Operation(summary = "Get area of law")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Area of Law not found")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<AreaOfLaw> getAreaOfLaw(@PathVariable Integer id) {
        return ok(adminAreasOfLawService.getAreaOfLaw(id));
    }

    /**
     * Create an area of law.
     * @param areaOfLaw The area of law to create
     * @return The created area of law
     */
    @PostMapping()
    @Operation(summary = "Create area of law")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Invalid Area of Law")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "409", description = "Area of Law already exists")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<AreaOfLaw> createAreaOfLaw(@RequestBody AreaOfLaw areaOfLaw) {
        return created(URI.create(StringUtils.EMPTY)).body(adminAreasOfLawService.createAreaOfLaw(areaOfLaw));
    }

    /**
     * Update an area of law.
     * @param areaOfLaw The area of law to update
     * @return The updated area of law
     */
    @PutMapping()
    @Operation(summary = "Update area of law")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "400", description = "Invalid Area of Law")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Area of Law not found")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<AreaOfLaw> updateAreaOfLaw(@RequestBody AreaOfLaw areaOfLaw) {
        return ok(adminAreasOfLawService.updateAreaOfLaw(areaOfLaw));
    }

    /**
     * Delete an area of law by id.
     * @param areaOfLawId The id of the area of law
     * @return The id of the deleted area of law
     */
    @DeleteMapping("/{areaOfLawId}")
    @Operation(summary = "Delete area of law")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Area of Law not found")
    @ApiResponse(responseCode = "409", description = "Area of Law in use")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<Integer> deleteAreaOfLaw(@PathVariable Integer areaOfLawId) {
        adminAreasOfLawService.deleteAreaOfLaw(areaOfLawId);
        return ok().body(areaOfLawId);
    }
}
