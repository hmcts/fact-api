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
            + "ORDER BY distance, name "
            + "LIMIT 10")
    List<CourtWithDistance> findNearestTen(@Param("lat") Double lat, @Param("lon") Double lon);

    @Query(nativeQuery = true,
        value = "SELECT *, (point(c.lon, c.lat) <@> point(:lon, :lat)) as distance "
            + "FROM search_court as c "
            + "JOIN search_courtareaoflaw caol ON caol.court_id = c.id "
            + "JOIN search_areaoflaw aol ON aol.id = caol.area_of_law_id "
            + "WHERE c.displayed AND UPPER(aol.name) = UPPER(:aol) "
            + "ORDER BY distance, c.name "
            + "LIMIT 10")
    List<CourtWithDistance> findNearestTenByAreaOfLaw(@Param("lat") Double lat, @Param("lon") Double lon, String aol);

    @Query(nativeQuery = true,
        value = "SELECT *, (point(c.lon, c.lat) <@> point(:lon, :lat)) as distance "
            + "FROM search_court as c "
            + "JOIN search_courtareaoflaw caol ON caol.court_id = c.id "
            + "JOIN search_areaoflaw aol ON aol.id = caol.area_of_law_id "
            + "LEFT JOIN search_courtpostcode cp ON cp.court_id = c.id "
            + "WHERE c.displayed "
            + "AND UPPER(aol.name) = UPPER(:aol) "
            + "AND UPPER(REPLACE(cp.postcode, ' ', '')) = UPPER(REPLACE(:postcode, ' ', '')) "
            + "ORDER BY distance, c.name "
            + "LIMIT 10")
    List<CourtWithDistance> findNearestTenByAreaOfLawAndPostcode(@Param("lat") Double lat, @Param("lon") Double lon, String aol, String postcode);


    @Query(nativeQuery = true,
        value = "SELECT *, (point(c.lon, c.lat) <@> point(:lon, :lat)) as distance "
            + "FROM search_court as c "
            + "JOIN search_courtlocalauthorityareaoflaw claaol ON claaol.court_id = c.id "
            + "JOIN search_localauthority la ON la.id = claaol.local_authority_id "
            + "JOIN search_areaoflaw aol ON aol.id = claaol.area_of_law_id "
            + "WHERE c.displayed "
            + "AND UPPER(aol.name) = UPPER(:aol) "
            + "AND UPPER(la.name) = UPPER(:localAuthority) "
            + "ORDER BY distance, c.name "
            + "LIMIT 10")
    List<CourtWithDistance> findNearestTenByAreaOfLawAndLocalAuthority(@Param("lat") Double lat, @Param("lon") Double lon, String aol, String localAuthority);
}
