package uk.gov.hmcts.dts.fact.controllers.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.CourtHistory;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtHistoryService;

import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@Validated
@RestController
@RequestMapping(
    path = "/admin/courts/history",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtHistoryController {

    private final AdminCourtHistoryService adminCourtHistoryService;

    @Autowired
    public AdminCourtHistoryController(final AdminCourtHistoryService adminCourtHistoryService) {
        this.adminCourtHistoryService = adminCourtHistoryService;
    }

    @GetMapping
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<CourtHistory>> getAllCourtHistory() {
        return ok(adminCourtHistoryService.getAllCourtHistory());
    }

    @GetMapping("/{courtHistoryId}")
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<CourtHistory> getCourtHistoryById(@PathVariable Integer courtHistoryId) {
        return ok(adminCourtHistoryService.getCourtHistoryById(courtHistoryId));
    }

    @GetMapping("/id/{courtId}")
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<CourtHistory>> getCourtHistoryByCourtId(@PathVariable Integer courtId) {
        return ok(adminCourtHistoryService.getCourtHistoryByCourtId(courtId));
    }

    @GetMapping("/name/{courtName}")
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<CourtHistory>> getCourtHistoryByCourtName(@PathVariable String courtName) {
        return ok(adminCourtHistoryService.getCourtHistoryByCourtName(courtName));
    }

    @PostMapping
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<CourtHistory> addCourtHistory(@Valid @RequestBody CourtHistory courtHistory) {
        return created(URI.create("/admin/courts/history"))
            .body(adminCourtHistoryService.addCourtHistory(courtHistory));
    }

    @PutMapping
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<CourtHistory> updateCourtHistory(@Valid @RequestBody CourtHistory courtHistory) {
        return ok(adminCourtHistoryService.updateCourtHistory(courtHistory));
    }

    @DeleteMapping("/{courtHistoryId}")
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<CourtHistory> deleteByCourtHistoryId(@PathVariable Integer courtHistoryId) {
        return ok(adminCourtHistoryService.deleteCourtHistoryById(courtHistoryId));
    }

    @DeleteMapping("/id/{courtId}")
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<CourtHistory>> deleteCourtHistoriesByCourtId(@PathVariable Integer courtId) {
        return ok(adminCourtHistoryService.deleteCourtHistoriesByCourtId(courtId));
    }
}
