package uk.gov.hmcts.dts.fact.migration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.ServiceCentre;
import uk.gov.hmcts.dts.fact.migration.model.CourtMigrationData;
import uk.gov.hmcts.dts.fact.migration.model.MigrationExportResponse;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MigrationPrivateDataServiceTest {

    @Mock
    private CourtRepository courtRepository;

    @InjectMocks
    private MigrationPrivateDataService migrationPrivateDataService;

    private Court court;

    @BeforeEach
    void setUp() {
        court = new Court();
        court.setId(12);
        court.setName("Test Court");
        court.setSlug("test-slug");
        court.setAlert("urgent notice");
        court.setDisplayed(Boolean.TRUE);
        court.setRegionId(9);
        court.setServiceCentre(new ServiceCentre());
        court.setCreatedAt(Timestamp.from(ZonedDateTime.of(2023, 1, 1, 10, 0, 0, 0, ZoneOffset.UTC).toInstant()));
        court.setUpdatedAt(Timestamp.from(ZonedDateTime.of(2023, 12, 3, 11, 43, 0, 0, ZoneOffset.UTC).toInstant()));
    }

    @Test
    void shouldReturnCourtMigrationDataForDisplayedCourtsOnly() {
        Court anotherCourt = new Court();
        anotherCourt.setId(34);
        anotherCourt.setName("Another Court");
        anotherCourt.setSlug("another-slug");
        anotherCourt.setAlert("notice");
        anotherCourt.setDisplayed(Boolean.FALSE);
        anotherCourt.setRegionId(5);
        anotherCourt.setCreatedAt(Timestamp.from(ZonedDateTime.of(2020, 5, 1, 9, 0, 0, 0, ZoneOffset.UTC).toInstant()));
        anotherCourt.setUpdatedAt(Timestamp.from(ZonedDateTime.of(2021, 6, 2, 14, 30, 0, 0, ZoneOffset.UTC).toInstant()));

        when(courtRepository.findAll()).thenReturn(List.of(court, anotherCourt));

        MigrationExportResponse response = migrationPrivateDataService.getCourtExport();

        assertThat(response.getCourts()).hasSize(1);
        CourtMigrationData first = response.getCourts().get(0);

        assertThat(first.getId()).isEqualTo("12");
        assertThat(first.getSlug()).isEqualTo("test-slug");
        assertThat(first.getOpen()).isTrue();
        assertThat(first.getTemporaryUrgentNotice()).isEqualTo("urgent notice");
        assertThat(first.getRegionId()).isEqualTo(9);
        assertThat(first.getServiceCentre()).isTrue();
    }
}
