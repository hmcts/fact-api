package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.dts.fact.entity.Court;

import java.util.List;
import java.util.Optional;

public interface CourtRepository extends JpaRepository<Court, Integer> {
    Optional<Court> findBySlug(String slug);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Court c SET c.info = :info, c.infoCy = :infoCy WHERE c.slug in :slugs")
    void updateInfoForSlugs(@Param("slugs") List<String> slugs, @Param("info") String info, @Param("infoCy") String infoCy);

    List<Court> findCourtBySlugStartingWithAndDisplayed(String prefix, boolean active);

    @Query(nativeQuery = true,
        value = "SELECT * FROM search_court c LEFT JOIN search_courtaddress ca ON ca.court_id = c.id AND ca.address_type_id != 5881 "
        + "WHERE displayed = true AND ("
        + "  CAST (c.cci_code AS text) ILIKE concat('%', :query, '%') "
        + "  OR CAST (c.number AS text) ILIKE concat('%', :query, '%') "
        + "  OR CAST (c.magistrate_code AS text) ILIKE concat('%', :query, '%') "
        + "  OR c.name ILIKE concat('%', :query, '%') "
        + "  OR c.name_cy ILIKE concat('%', :query, '%') "
        + "  OR ca.address ILIKE concat('%', :query, '%') "
        + "  OR ca.address_cy ILIKE concat('%', :query, '%') "
        + "  OR ca.town_name ILIKE concat('%', :query, '%') "
        + "  OR ca.town_name_cy ILIKE concat('%', :query, '%') "
        + "  OR REPLACE(ca.postcode, ' ', '') ILIKE REPLACE(concat('%', :query, '%'), ' ', '') "
        + ") "
        + "ORDER BY "
        + "  CASE WHEN REPLACE(COALESCE(ca.postcode, ''), ' ', '') ILIKE REPLACE(concat('%', :query, '%'), ' ', '') THEN 1 ELSE 0 END DESC, "
        + "  CASE WHEN "
        + "    COALESCE(CAST(c.cci_code AS text), '') ILIKE concat('%', :query, '%') "
        + "    OR COALESCE(CAST(c.number AS text), '') ILIKE concat('%', :query, '%') "
        + "    OR COALESCE(CAST(c.magistrate_code AS text), '') ILIKE concat('%', :query, '%') "
        + "    THEN 1 ELSE 0 END DESC, "
        + "  CASE WHEN COALESCE(c.name, '') ILIKE concat('%', :query, '%') OR COALESCE(c.name_cy, '') ILIKE concat('%', :query, '%') THEN 1 ELSE 0 END DESC, "
        + "  CASE WHEN COALESCE(ca.town_name, '') ILIKE concat('%', :query, '%') OR COALESCE(ca.town_name_cy, '') ILIKE concat('%', :query, '%') THEN 1 ELSE 0 END DESC, "
        + "  CASE WHEN COALESCE(ca.address, '') ILIKE concat('%', :query, '%') OR COALESCE(ca.address_cy, '') ILIKE concat('%', :query, '%') THEN 1 ELSE 0 END DESC, "
        + "  name")
    List<Court> queryBy(String query);


}
