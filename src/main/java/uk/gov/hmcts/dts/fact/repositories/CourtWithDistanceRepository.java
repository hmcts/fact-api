package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;

import java.util.List;

public interface CourtWithDistanceRepository extends JpaRepository<CourtWithDistance, Integer> {
    String SELECT_POINT_C_LON_C_LAT_POINT_LON_LAT_AS_DISTANCE = "SELECT *, (point(c.lon, c.lat) <@> point(:lon, :lat)) as distance";
    String FROM_SEARCH_COURT_AS_C = "FROM search_court as c ";
    String WHERE_C_DISPLAYED = "WHERE c.displayed ";
    String ORDER_BY_DISTANCE_C_NAME = "ORDER BY distance, c.name ";
    String LIMIT_10 = "LIMIT 10";
    String LIMIT_1 = "LIMIT 1";
    String LAT = "lat";
    String LON = "lon";

    @Query(nativeQuery = true,
        value = SELECT_POINT_C_LON_C_LAT_POINT_LON_LAT_AS_DISTANCE + " "
            + FROM_SEARCH_COURT_AS_C
            + WHERE_C_DISPLAYED
            + "ORDER BY distance, name "
            + LIMIT_10)
    List<CourtWithDistance> findNearestTen(@Param(LAT) Double lat, @Param(LON) Double lon);

    @Query(nativeQuery = true,
        value = SELECT_POINT_C_LON_C_LAT_POINT_LON_LAT_AS_DISTANCE + " "
            + FROM_SEARCH_COURT_AS_C
            + "JOIN search_courtareaoflaw caol ON caol.court_id = c.id "
            + "JOIN search_areaoflaw aol ON aol.id = caol.area_of_law_id "
            + "WHERE c.displayed AND UPPER(aol.name) = UPPER(:aol) "
            + ORDER_BY_DISTANCE_C_NAME
            + LIMIT_10)
    List<CourtWithDistance> findNearestTenByAreaOfLaw(@Param(LAT) Double lat, @Param(LON) Double lon, String aol);

    @Query(nativeQuery = true,
        value = SELECT_POINT_C_LON_C_LAT_POINT_LON_LAT_AS_DISTANCE + " "
            + FROM_SEARCH_COURT_AS_C
            + "JOIN search_courtareaoflaw caol ON caol.court_id = c.id "
            + "JOIN search_areaoflaw aol ON aol.id = caol.area_of_law_id "
            + "JOIN search_courtpostcode cp ON cp.court_id = c.id "
            + WHERE_C_DISPLAYED
            + "AND UPPER(aol.name) = UPPER(:aol) "
            + "AND UPPER(REPLACE(cp.postcode, ' ', '')) = UPPER(REPLACE(:postcode, ' ', '')) "
            + ORDER_BY_DISTANCE_C_NAME
            + LIMIT_10)
    List<CourtWithDistance> findNearestTenByAreaOfLawAndCourtPostcode(@Param(LAT) Double lat, @Param(LON) Double lon, String aol, String postcode);

    @Query(nativeQuery = true,
        value = SELECT_POINT_C_LON_C_LAT_POINT_LON_LAT_AS_DISTANCE + " "
            + FROM_SEARCH_COURT_AS_C
            + "JOIN search_courtlocalauthorityareaoflaw claaol ON claaol.court_id = c.id "
            + "JOIN search_localauthority la ON la.id = claaol.local_authority_id "
            + "JOIN search_areaoflaw aol ON aol.id = claaol.area_of_law_id "
            + WHERE_C_DISPLAYED
            + "AND UPPER(aol.name) = UPPER(:aol) "
            + "AND UPPER(la.name) = UPPER(:localAuthority) "
            + ORDER_BY_DISTANCE_C_NAME
            + LIMIT_10)
    List<CourtWithDistance> findNearestTenByAreaOfLawAndLocalAuthority(@Param(LAT) Double lat, @Param(LON) Double lon, String aol, String localAuthority);

    @Query(nativeQuery = true,
        value = SELECT_POINT_C_LON_C_LAT_POINT_LON_LAT_AS_DISTANCE + " "
            + FROM_SEARCH_COURT_AS_C
            + "JOIN search_courtlocalauthorityareaoflaw claaol ON claaol.court_id = c.id "
            + "JOIN search_localauthority la ON la.id = claaol.local_authority_id "
            + "JOIN search_areaoflaw aol ON aol.id = claaol.area_of_law_id "
            + "JOIN search_serviceareacourt sac ON sac.court_id = c.id "
            + WHERE_C_DISPLAYED
            + "AND UPPER(aol.name) = UPPER(:aol) "
            + "AND UPPER(la.name) = UPPER(:localAuthority) "
            + "AND sac.catchment_type = 'regional' "
            + ORDER_BY_DISTANCE_C_NAME
            + LIMIT_1)
    List<CourtWithDistance> findNearestRegionalByAreaOfLawAndLocalAuthority(@Param(LAT) Double lat, @Param(LON) Double lon, String aol, String localAuthority);
    
    @Query(nativeQuery = true,
        value = SELECT_POINT_C_LON_C_LAT_POINT_LON_LAT_AS_DISTANCE + " "
            + FROM_SEARCH_COURT_AS_C
            + "JOIN search_courtlocalauthorityareaoflaw claaol ON claaol.court_id = c.id "
            + "JOIN search_localauthority la ON la.id = claaol.local_authority_id "
            + "JOIN search_areaoflaw aol ON aol.id = claaol.area_of_law_id "
            + "JOIN search_serviceareacourt sac ON sac.court_id = c.id "
            + WHERE_C_DISPLAYED
            + "AND UPPER(aol.name) = UPPER(:aol) "
            + "AND sac.catchment_type = 'regional' "
            + ORDER_BY_DISTANCE_C_NAME
            + LIMIT_1)
    List<CourtWithDistance> findNearestRegionalByAreaOfLaw(@Param(LAT) Double lat, @Param(LON) Double lon, String aol);
}
