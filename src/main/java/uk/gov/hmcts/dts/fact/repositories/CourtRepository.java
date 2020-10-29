package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;

import java.util.List;
import java.util.Optional;

public interface CourtRepository extends JpaRepository<Court, Integer> {
    Optional<Court> findBySlug(String slug);

    @Query("SELECT c FROM Court c INNER JOIN c.addresses ca "
        + "WHERE LOWER(c.name) LIKE LOWER(concat('%', :query, '%')) "
        + "OR LOWER(c.nameCy) LIKE LOWER(concat('%', :query, '%')) "
        + "OR LOWER(ca.address) LIKE LOWER(concat('%', :query, '%')) "
        + "OR LOWER(ca.addressCy) LIKE LOWER(concat('%', :query, '%')) "
        + "OR LOWER(ca.townName) LIKE LOWER(concat('%', :query, '%')) "
        + "OR LOWER(ca.townNameCy) LIKE LOWER(concat('%', :query, '%')) "
        + "OR REPLACE(LOWER(ca.postcode), ' ', '') LIKE REPLACE(LOWER(concat('%', :query, '%')), ' ', '')")
    List<Court> queryBy(String query);

    @Query(nativeQuery = true, name = "CourtWithDistance.findNearestCourts")
    List<CourtWithDistance> findNearest(@Param("lat") Double lat, @Param("lon") Double lon);
}
