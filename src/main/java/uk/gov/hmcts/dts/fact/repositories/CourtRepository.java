package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.dts.fact.entity.Court;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface CourtRepository extends JpaRepository<Court, Integer> {
    Optional<Court> findBySlug(String slug);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Court c SET c.info = :info, c.infoCy = :infoCy WHERE c.slug in :slugs")
    void updateInfoForSlugs(@Param("slugs") List<String> slugs, @Param("info") String info, @Param("infoCy") String infoCy);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Court SET lat = :lat, lon = :lon WHERE slug = :slug")
    void updateLatLonBySlug(String slug, Double lat, Double lon);

    @Query(value = "UPDATE search_court SET image_file = :imageFile WHERE slug = :slug RETURNING image_file", nativeQuery = true)
    String updateCourtImageBySlug(String slug, String imageFile);

    List<Court> findCourtByNameStartingWithIgnoreCaseAndDisplayedOrderByNameAsc(String prefix, boolean active);

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

    /**
     * Searching by court name, town name or court address using fuzzy matching.
     * - The court name and court address are searched using Postgres trigram word similarity with a cut off threshold of 0.6. Any matches below
     *   the threshold will not be accepted.
     * - For input where the first word matches the the start of the court name, we allow a lower trigram threshold of 0.5.
     * - The town name is searched using Levenshtein distance. The match rate is calculated using the length of town subtract the Levenshtein distance
     *   divided by the length of town. Only match rate above the cut-off threshold of 0.79 will be accepted. This means for a town of length 5-9, 1 typo will be accepted.
     *   For a town of length 10-14, 2 typos will be accepted, etc. Note that with Levenshtein distance, 2-letter transposition will be counted as 2 typos.
     * Sorting of the results is in the following order, with good matches followed by close matches:
     * - Good name match - Courts with good court name match (word similarity more than 0.85) will be sorted next.
     * - Good town match - Courts with lower town name levenshtein distance will be sorted next.
     * - Good address match - Courts with good court address match (word similarity more than 0.85) will be sorted next.
     * - Court name match where the first word on input matches the start of the court name.
     * - Close match - The remaining courts will be sorted in alphabetical order.
     *
     * @param query the search string
     * @return a list of courts matching the search string
     */
    @Query(nativeQuery = true,
        value = "SELECT *,"
            + "    CASE WHEN length(ca.town_name) > 0 AND (ca.town_name_cy IS NULL OR ca.town_name_cy = '') THEN levenshtein(lower(ca.town_name), lower(:query))"
            + "         WHEN length(ca.town_name_cy) > 0 AND (ca.town_name IS NULL OR ca.town_name = '') THEN levenshtein(lower(ca.town_name_cy), lower(:query))"
            + "         ELSE lEAST(levenshtein(lower(ca.town_name), lower(:query)), levenshtein(lower(ca.town_name_cy), lower(:query))) END"
            + "    AS town_diff"
            + "  FROM search_court c LEFT JOIN search_courtaddress ca"
            + "    ON ca.court_id = c.id AND ca.address_type_id != 5881"
            + "  WHERE displayed = true AND ("
            + "    (:query <% c.name AND word_similarity(:query, c.name) > 0.6)"
            + "    OR (:query <% c.name_cy AND word_similarity(:query, c.name_cy) > 0.6)"
            + "    OR (c.name ILIKE concat(split_part(:query, ' ' , 1), '%') AND word_similarity(:query, c.name) > 0.5)"
            + "    OR (c.name_cy ILIKE concat(split_part(:query, ' ' , 1), '%') AND word_similarity(:query, c.name_cy) > 0.5)"
            + "    OR (:query <% ca.address AND word_similarity(:query, ca.address) > 0.6)"
            + "    OR (:query <% ca.address_cy AND word_similarity(:query, ca.address_cy) > 0.6)"
            + "    OR (length(ca.town_name) > 0 AND CAST((length(ca.town_name) - levenshtein(lower(ca.town_name), lower(:query))) AS float) / length(ca.town_name) > 0.79)"
            + "    OR (length(ca.town_name_cy) > 0 AND CAST((length(ca.town_name_cy) - levenshtein(lower(ca.town_name_cy), lower(:query))) AS float) / length(ca.town_name_cy) > 0.79))"
            + "  ORDER BY"
            + "    CASE WHEN word_similarity(:query, c.name) > 0.85 OR word_similarity(:query, c.name_cy) > 0.85 THEN 1 ELSE 0 END DESC,"
            + "    town_diff,"
            + "    CASE WHEN word_similarity(:query, ca.address) > 0.85 OR word_similarity(:query, ca.address_cy) > 0.85 THEN 1 ELSE 0 END DESC,"
            + "    CASE WHEN COALESCE(c.name, '') ILIKE concat(split_part(:query, ' ' , 1), '%') OR COALESCE(c.name_cy, '') ILIKE concat(split_part(:query, ' ' , 1), '%') THEN 1 ELSE 0 END DESC,"
            + "    name")
    List<Court> findCourtByNameAddressOrTownFuzzyMatch(String query);

    /**
     * Searching by court name, town name, court address or partial postcode by exact match only (i.e. the input search string matches all/part of the record in the database).
     * Notes: Punctuations are stripped off before the comparison, and casing are ignored.
     * Sorting of the results is in the following order:
     * - Exact match to the court name in alphabetical order
     * - Exact match to the town name in alphabetical order
     * - Exact match to the court address in alphabetical order
     * - The remaining courts in alphabetical order
     *
     * @param query the search string
     * @return a list of courts matching the search string
     */
    @Query(nativeQuery = true,
        value = "SELECT * FROM search_court c LEFT JOIN search_courtaddress ca"
            + "    ON ca.court_id = c.id AND ca.address_type_id != 5881"
            + "  WHERE displayed = true AND ("
            + "    regexp_replace(c.name, '[^A-Za-z0-9]+', '', 'g') ILIKE concat('%', :query, '%')"
            + "    OR regexp_replace(c.name_cy, '[^A-Za-z0-9]+', '', 'g') ILIKE concat('%', :query, '%')"
            + "    OR regexp_replace(ca.address, '[^A-Za-z0-9]+', '', 'g') ILIKE concat('%', :query, '%')"
            + "    OR regexp_replace(ca.address_cy, '[^A-Za-z0-9]+', '', 'g') ILIKE concat('%', :query, '%')"
            + "    OR regexp_replace(ca.town_name, '[^A-Za-z0-9]+', '', 'g') ILIKE concat('%', :query, '%')"
            + "    OR regexp_replace(ca.town_name_cy, '[^A-Za-z0-9]+', '', 'g') ILIKE concat('%', :query, '%')"
            + "    OR regexp_replace(ca.postcode, '[^A-Za-z0-9]+', '', 'g') ILIKE concat('%', :query, '%'))"
            + "  ORDER BY"
            + "    name, "
            + "    CASE WHEN COALESCE(regexp_replace(c.name, '[^A-Za-z0-9]+', '', 'g'), '') ILIKE concat('%', :query, '%')"
            + "           OR COALESCE(regexp_replace(c.name_cy, '[^A-Za-z0-9]+', '', 'g'), '') ILIKE concat('%', :query, '%')"
            + "         THEN 1 ELSE 0 END DESC,"
            + "    CASE WHEN COALESCE(regexp_replace(ca.town_name, '[^A-Za-z0-9]+', '', 'g'), '') ILIKE concat('%', :query, '%')"
            + "           OR COALESCE(regexp_replace(ca.town_name_cy, '[^A-Za-z0-9]+', '', 'g'), '') ILIKE concat('%', :query, '%')"
            + "         THEN 1 ELSE 0 END DESC,"
            + "    CASE WHEN COALESCE(regexp_replace(ca.address, '[^A-Za-z0-9]+', '', 'g'), '') ILIKE concat('%', :query, '%')"
            + "           OR COALESCE(regexp_replace(ca.address_cy, '[^A-Za-z0-9]+', '', 'g'), '') ILIKE concat('%', :query, '%')"
            + "         THEN 1 ELSE 0 END DESC")
    List<Court> findCourtByNameAddressTownOrPartialPostcodeExactMatch(String query);

    /**
     * Searching by court code. Only exact match to court code is accepted.
     * @param code the court code
     * @return a list of courts matching the input court code
     */
    @Query(nativeQuery = true,
        value = "SELECT * FROM search_court c LEFT JOIN search_courtaddress ca"
            + "    ON ca.court_id = c.id AND ca.address_type_id != 5881"
            + "  WHERE displayed = true AND ("
            + "    c.cci_code = :code"
            + "    OR c.number = :code"
            + "    OR c.magistrate_code = :code)"
            + "  ORDER BY name")
    List<Court> findCourtByCourtCode(Integer code);

    /**
     * Searching by full court postcode.
     * @param postcode the postcode
     * @return a list of courts matching the input postcode
     */
    @Query(nativeQuery = true,
        value = "SELECT * FROM search_court c LEFT JOIN search_courtaddress ca"
            + "    ON ca.court_id = c.id AND ca.address_type_id != 5881"
            + "  WHERE displayed = true"
            + "    AND REPLACE(ca.postcode, ' ', '') = UPPER(REPLACE(:postcode, ' ', ''))"
            + "  ORDER BY name")
    List<Court> findCourtByFullPostcode(String postcode);
}
