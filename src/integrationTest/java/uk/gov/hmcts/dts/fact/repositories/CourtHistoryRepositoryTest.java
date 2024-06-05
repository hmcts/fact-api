package uk.gov.hmcts.dts.fact.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.entity.CourtHistory;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class CourtHistoryRepositoryTest {

    private CourtHistory courtHistory1 = new CourtHistory(
            null, 1479805, "Old Neath Court",LocalDateTime.parse("2024-02-03T10:15:30"),
            LocalDateTime.parse("2024-02-03T10:15:30"), null);
    private CourtHistory courtHistory2 = new CourtHistory(
            null, 1479805, "Old Neath Court", LocalDateTime.parse("2024-02-03T10:15:30"), LocalDateTime.parse("2024-02-03T10:15:30"), null);
    private CourtHistory courtHistory3 = new CourtHistory(
            3, 1479802, "fakeCourt3", LocalDateTime.parse("2024-02-03T10:15:30"),
            LocalDateTime.parse("2023-12-03T11:15:30"), "");
    private CourtHistory courtHistory4 = new CourtHistory(
            4, 1479802, "fakeCourt4", LocalDateTime.parse("2024-02-03T10:15:30"),
            LocalDateTime.parse("2024-03-03T10:15:30"), "Llys y Goron");

    @Autowired
    CourtHistoryRepository courtHistoryRepository;

    @Test
    void shouldFindCourtHistoriesByCourtId() {
        courtHistoryRepository.save(courtHistory1);
        courtHistoryRepository.save(courtHistory2);
        courtHistoryRepository.save(courtHistory3);
        courtHistoryRepository.save(courtHistory4);

        assertThat(courtHistoryRepository.findAllBySearchCourtId(1479805))
            .hasSize(2)
                .satisfiesExactly(
                    savedCourtHistoryItem1 ->
                        assertThat(savedCourtHistoryItem1.getSearchCourtId()).isEqualTo(1479805),
                    savedCourtHistoryItem2 ->
                        assertThat(savedCourtHistoryItem2.getSearchCourtId()).isEqualTo(1479805)
                );
    }

    @Test
    void shouldNotFindCourtIfNoCourtHistoriesHaveCourtID() {

        assertThat(courtHistoryRepository.findAllBySearchCourtId(26)).isEmpty();
    }

    @Test
    void shouldOnlyReturnCourtHistoriesThatMatchGivenCourtName() {
        courtHistoryRepository.save(courtHistory1);
        courtHistoryRepository.save(courtHistory2);
        courtHistoryRepository.save(courtHistory3);
        courtHistoryRepository.save(courtHistory4);

        assertThat(courtHistoryRepository.findAllByCourtName("Old Neath Court"))
            .hasSize(2)
            .satisfiesExactly(
                savedCourtHistoryItem1 ->
                    assertThat(savedCourtHistoryItem1.getCourtName()).isEqualTo("Old Neath Court"),
                savedCourtHistoryItem2 ->
                    assertThat(savedCourtHistoryItem2.getCourtName()).isEqualTo("Old Neath Court")
            );
    }

    @Test
    void shouldReturnCourtHistoriesThatMatchTheGivenNameIgnoringCase() {
        courtHistoryRepository.save(courtHistory1);
        courtHistoryRepository.save(courtHistory2);
        courtHistoryRepository.save(courtHistory3);
        courtHistoryRepository.save(courtHistory4);

        assertThat(courtHistoryRepository.findAllByCourtNameIgnoreCase("old NEAth courT"))
            .hasSize(2)
            .satisfiesExactly(
                savedCourtHistoryItem1 ->
                    assertThat(savedCourtHistoryItem1.getCourtName()).isEqualTo("Old Neath Court"),
                savedCourtHistoryItem2 ->
                    assertThat(savedCourtHistoryItem2.getCourtName()).isEqualTo("Old Neath Court")
            );
    }

    @Test
    void shouldNotReturnAnyCourtHistoryIfGivenNameIsNotAMatch() {
        assertThat(courtHistoryRepository.findAllByCourtName("Old Neath Court")).isEmpty();
    }

    @Test
    void shouldDeleteAllTheCourtHistoriesOfACourt() {
        courtHistoryRepository.save(courtHistory1);
        courtHistoryRepository.save(courtHistory2);
        courtHistoryRepository.save(courtHistory3);
        courtHistoryRepository.save(courtHistory4);

        assertThat(courtHistoryRepository.findAll())
            .hasSize(4);

        assertThat(courtHistoryRepository.deleteCourtHistoriesBySearchCourtId(1479805))
            .hasSize(2)
            .satisfiesExactly(
                deletedCourtHistoryItem1 ->
                    assertThat(deletedCourtHistoryItem1.getSearchCourtId()).isEqualTo(1479805),
                deletedCourtHistoryItem2 ->
                    assertThat(deletedCourtHistoryItem2.getSearchCourtId()).isEqualTo(1479805)
            );

        assertThat(courtHistoryRepository.findAll())
            .hasSize(2);
    }

    @Test
    void shouldNotDeleteAnyCourtHistoriesIfNoneMatchSearchCourtID() {
        courtHistoryRepository.save(courtHistory1);
        courtHistoryRepository.save(courtHistory2);
        courtHistoryRepository.save(courtHistory3);
        courtHistoryRepository.save(courtHistory4);

        assertThat(courtHistoryRepository.deleteCourtHistoriesBySearchCourtId(0))
            .isEmpty();

        assertThat(courtHistoryRepository.findAll())
            .hasSize(4);
    }
}
