package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtHistory;

import java.util.List;

public interface CourtHistoryRepository extends JpaRepository<CourtHistory, Integer> {

    List<CourtHistory> findAllBySearchCourtId(Integer searchCourtId);

    List<CourtHistory> findAllByCourtName(String courtName);

    List<CourtHistory> deleteCourtHistoriesBySearchCourtId(Integer searchCourtId);

    List<CourtHistory> findCourtByCourtNameStartingWithIgnoreCaseOrderByCourtNameAsc(String prefix);

//    @Query(value = "SELECT * FROM admin_court_history a WHERE ?1 % ANY(STRING_TO_ARRAY(a.court_name,' '))"
//        + "ORDER BY CASE WHEN word_similarity(:query, a.court_name) > 0.85 DESC", nativeQuery = true)
@Query(value = "WITH split AS ( "
    + "  SELECT (string_to_array(btrim(:query),' '))[1] AS first_word"
    + ") "
    + "SELECT * FROM admin_court_history a WHERE levenshtein(LOWER(a.court_name), LOWER(:query)) <= 5 "
    + "OR a.court_name ILIKE concat('%', (select first_word from split), '%') "
    + "OR (a.court_name ILIKE concat(split_part(:query, ' ' , 1), '%') AND word_similarity(:query, a.court_name) > 0.5) "
    + "OR SOUNDEX(a.court_name) = SOUNDEX(:query) "
    + "OR word_similarity(LOWER(:query), LOWER(a.court_name)) > 0.8 "
    + "ORDER BY LOWER(a.court_name) <->> LOWER(:query)", nativeQuery = true)
    List<CourtHistory> findCourtByNameFuzzyMatch(String query);
}

