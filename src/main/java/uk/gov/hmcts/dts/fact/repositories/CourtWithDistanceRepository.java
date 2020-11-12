package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;

import java.util.List;

public interface CourtWithDistanceRepository extends JpaRepository<CourtWithDistance, Integer> {
    @Query(nativeQuery = true,
        value = "SELECT *, (point(c.lon, c.lat) <@> point(:lon, :lat)) as distance "
            + "FROM search_court as c "
            + "WHERE c.displayed "
            + "ORDER BY distance, name limit 10")
    List<CourtWithDistance> findNearestTen(@Param("lat") Double lat, @Param("lon") Double lon);

    @Query(nativeQuery = true,
        value = "SELECT *, (point(c.lon, c.lat) <@> point(:lon, :lat)) as distance "
            + "FROM search_court as c "
            + "INNER JOIN search_courtareaoflaw caol ON caol.court_id = c.id "
            + "INNER JOIN search_areaoflaw aol ON aol.id = caol.area_of_law_id "
            + "WHERE c.displayed AND UPPER(aol.name) = UPPER(:aol) "
            + "ORDER BY distance, c.name limit 10")
    List<CourtWithDistance> findNearestTenByAreaOfLaw(@Param("lat") Double lat, @Param("lon") Double lon, String aol);
}
