package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "court_road_distance_cache")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourtRoadDistanceCache {
    @Id
    private String postcode;

    private String responseJson;

    private Instant computedAt;
}
