package uk.gov.hmcts.dts.fact.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.clients.AzureMapsRouteMatrixClient;
import uk.gov.hmcts.dts.fact.entity.CourtByRoadDistance;
import uk.gov.hmcts.dts.fact.entity.CourtRoadDistanceCache;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.exception.InvalidPostcodeException;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.Feature;
import uk.gov.hmcts.dts.fact.model.Geometry;
import uk.gov.hmcts.dts.fact.model.MatrixCell;
import uk.gov.hmcts.dts.fact.model.RouteMatrixRequest;
import uk.gov.hmcts.dts.fact.model.RouteMatrixResponse;
import uk.gov.hmcts.dts.fact.repositories.CourtRoadDistanceCacheRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureMapsService {

    private static final String API_VERSION = "2025-01-01";

    private final CourtWithDistanceRepository courtWithDistanceRepository;
    private final CourtRoadDistanceCacheRepository courtRoadDistanceCacheRepository;
    private final MapitService mapitService;
    private final ObjectMapper objectMapper;

    private final AzureMapsRouteMatrixClient routeMatrixClient;

    /**
     * Find nearest courts by road distance.
     */
    public List<CourtByRoadDistance> findNearestCourtsByRoad(String postcode, int numberOfCrowCourts) {
        String normalisedPostcode = postcode.replaceAll("\\s+", "").toUpperCase();
        Optional<CourtRoadDistanceCache> cachedResults = courtRoadDistanceCacheRepository.findById(normalisedPostcode);

        if (cachedResults.isPresent()) {
            try {
                log.info("Using cached results");
                JavaType type = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, CourtByRoadDistance.class);
                return objectMapper.readValue(cachedResults.get().getResponseJson(), type);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error deserializing court road distance results", e);
            }
        }

        MapitData origin = mapitService.getMapitData(postcode).orElseThrow(() -> new InvalidPostcodeException(postcode));

        List<CourtWithDistance> crowFlightCourts = courtWithDistanceRepository.findNearestX(
            origin.getLat(),
            origin.getLon(),
            numberOfCrowCourts
        );

        RouteMatrixResponse matrix = calculateRoadDistances(origin, crowFlightCourts);
        List<CourtByRoadDistance> results = mapMatrixToResults(matrix, crowFlightCourts);

        List<CourtByRoadDistance> sortedResults = results.stream()
            .sorted(Comparator.comparingDouble(CourtByRoadDistance::getDistanceMiles))
            .limit(10)
            .toList();

        storeResultsForPostcode(normalisedPostcode, sortedResults);

        return sortedResults;
    }

    /**
     * Call Azure Maps Route Matrix (2025-01-01) via Feign.
     */
    private RouteMatrixResponse calculateRoadDistances(MapitData origin, List<CourtWithDistance> courts) {
        Feature originFeature = new Feature(new Geometry(List.of(List.of(origin.getLon(), origin.getLat()))),
                                            Map.of("pointType", "origins"));

        List<List<Double>> destinationCoords = courts.stream()
            .map(c -> List.of(c.getLon(), c.getLat()))
            .toList();

        Feature destinationsFeature = new Feature(new Geometry(destinationCoords), Map.of("pointType", "destinations"));

        RouteMatrixRequest request = new RouteMatrixRequest();
        request.setFeatures(List.of(originFeature, destinationsFeature));
        log.info("Calling Azure Maps Route Matrix");
        return routeMatrixClient.calculateMatrix(
            API_VERSION,
            request
        );
    }

    /**
     * Map the new Matrix response to CourtByRoadDistance.
     */
    private List<CourtByRoadDistance> mapMatrixToResults(RouteMatrixResponse response, List<CourtWithDistance> courts) {
        List<CourtByRoadDistance> results = new ArrayList<>();

        for (MatrixCell cell : response.getProperties().getMatrix()) {
            if (cell.getStatusCode() != 200) {
                log.warn(
                    "RouteMatrix cell failed: destinationIndex={}, statusCode={}",
                    cell.getDestinationIndex(),
                    cell.getStatusCode()
                );
                continue;
            }

            CourtWithDistance court = courts.get(cell.getDestinationIndex());

            results.add(new CourtByRoadDistance(
                court.getId(),
                court.getName(),
                court.getSlug(),
                court.getLat(),
                court.getLon(),
                cell.getDistanceInMeters() * 0.000621371,
                cell.getDurationInSeconds() / 60.0
            ));
        }

        return results;
    }

    private void storeResultsForPostcode(String postcode, List<CourtByRoadDistance> results) {
        try {
            courtRoadDistanceCacheRepository.save(
                new CourtRoadDistanceCache(
                    postcode,
                    objectMapper.writeValueAsString(results),
                    Instant.now()
                )
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing court road distance results", e);
        }
    }
}
