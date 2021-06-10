package uk.gov.hmcts.dts.fact.services;

import com.fasterxml.jackson.databind.JsonNode;
import feign.FeignException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.mapit.MapitClient;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.mapit.MapitDataAreaDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MapitService {

    private final Logger logger;

    private final MapitClient mapitClient;

    @Autowired
    public MapitService(final Logger logger, final MapitClient mapitClient) {
        this.logger = logger;
        this.mapitClient = mapitClient;
    }

    public Optional<MapitData> getMapitData(final String postcode) {

        if (!postcode.isBlank()) {
            try {
                final MapitData mapitData = mapitClient.getMapitData(postcode);

                if (mapitData.hasLatAndLonValues()) {
                    return Optional.of(mapitData);
                }
            } catch (final FeignException ex) {
                logger.warn("HTTP Status: {} Message: {}", ex.status(), ex.getMessage(), ex);
            }
        }

        return Optional.empty();
    }

    public Optional<MapitData> getMapitDataWithPartial(final String postcode) {

        if (!postcode.isBlank()) {
            try {
                final MapitData mapitData = mapitClient.getMapitDataWithPartial(postcode);

                if (mapitData.hasLatAndLonValues()) {
                    return Optional.of(mapitData);
                }
            } catch (final FeignException ex) {
                logger.warn("HTTP Status: {} Message: {}", ex.status(), ex.getMessage(), ex);
            }
        }

        return Optional.empty();
    }

    public Optional<List<MapitDataAreaDetails>> getMapitDataWithArea(final String area) {

        if (!area.isBlank()) {
            try {

                JsonNode jsonNode = mapitClient.getMapitDataWithArea(area);

                List<MapitDataAreaDetails> list = new ArrayList<>();
                for(JsonNode value : jsonNode) {
                    list.add(new MapitDataAreaDetails(
                      value.get("id").asText(),
                      value.hasNonNull("parent_area") ?
                          value.asText("parent_area"): "",
                      Integer.parseInt(value.get("generation_high").asText()),
                      value.get("name").asText(), value.get("country").asText(),
                      value.get("type_name").asText(), value.get("generation_low").asText(),
                      value.get("country_name").asText(), value.get("type").asText()));
                }

                return Optional.of(list);
            } catch (final FeignException ex) {
                logger.warn("HTTP Status: {} Message: {}", ex.status(), ex.getMessage(), ex);
            }
        }

        logger.warn("area was blank: {}", area);
        return Optional.empty();
    }
}
