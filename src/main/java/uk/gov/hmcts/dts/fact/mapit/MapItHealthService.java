package uk.gov.hmcts.dts.fact.mapit;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.dts.fact.exception.MapitUsageException;

import java.io.IOException;

@Component
@Slf4j
@SuppressWarnings("PMD.ImmutableField")
public class MapItHealthService {
    static final String QUOTA = "quota";
    static final String LIMIT = "limit";
    static final String CURRENT = "current";

    @Value("${mapit.url}")
    private String mapitUrl;

    @Value("${mapit.endpoint.quota}")
    private String mapitQuotaPath;

    @Value("${mapit.key}")
    private String mapitKey;

    private RestTemplate restTemplate = new RestTemplate();

    public boolean isUp() throws IOException {
        final String fullPath = mapitUrl + mapitQuotaPath + "?api_key=" + mapitKey;
        final ResponseEntity<JsonNode> response = restTemplate.getForEntity(fullPath, JsonNode.class);
        final String envValue = System.getenv("MAPIT_KEY");

        log.info("xxxxxx - key is " + envValue);

        log.info("*******health-service - key is " + mapitKey);
        log.info("*******health-service - mapit url is " + mapitUrl);
        log.info("*******health-service - mapit quota url is " + mapitQuotaPath);

        if (StringUtils.isNotBlank(mapitKey)) {
            log.info("******health-service - mapit key present!!!!!********");
        }

        final JsonNode responseBody = response.getBody();
        if (responseBody != null) {
            final JsonNode quota = responseBody.get(QUOTA);
            if (quota != null) {
                validateQuotaLimit(quota);
            }
        }
        return response.getStatusCode().equals(HttpStatus.OK);
    }

    private void validateQuotaLimit(final JsonNode quota) {
        final int limit = quota.get(LIMIT).asInt();
        // Mapit quota limit will be zero if a valid Mapit key has been configured. If no key is supplied and the
        // limit hasn't been reached, throw an exception so the Mapit service can be marked as 'down' for health check
        if (limit != 0 && limit <= quota.get(CURRENT).asInt()) {
            throw new MapitUsageException();
        }
    }
}
