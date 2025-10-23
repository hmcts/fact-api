package uk.gov.hmcts.dts.fact.migration.service;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.migration.model.CourtMigrationData;
import uk.gov.hmcts.dts.fact.migration.model.MigrationExportResponse;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MigrationPrivateDataService {

    private final CourtRepository courtRepository;

    public MigrationPrivateDataService(final CourtRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    public MigrationExportResponse getCourtExport() {
        List<CourtMigrationData> courts = courtRepository.findAll()
            .stream()
            .filter(court -> Boolean.TRUE.equals(court.getDisplayed()))
            .map(this::mapCourt)
            .collect(Collectors.toList());
        return new MigrationExportResponse(courts);
    }

    private CourtMigrationData mapCourt(final Court court) {
        return new CourtMigrationData(
            court.getId() == null ? null : court.getId().toString(),
            court.getName(),
            court.getSlug(),
            Boolean.TRUE.equals(court.getDisplayed()),
            court.getAlert(),
            toOffsetDateTime(court.getCreatedAt()),
            toOffsetDateTime(court.getUpdatedAt()),
            court.getRegionId(),
            court.getServiceCentre() != null
        );
    }

    private OffsetDateTime toOffsetDateTime(final Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant().atOffset(ZoneOffset.UTC);
    }
}
