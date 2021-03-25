package uk.gov.hmcts.dts.fact.mapit;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MapItService {
    @Value("${mapit.url}")
    private String mapitUrl;

    @Value("${mapit.endpoint.quota}")
    private String mapitQuotaPath;

    public boolean isUp() {
        final ResponseEntity<JsonNode> responseEntity = new RestTemplate().getForEntity(getMapitQuotaEndpoint(), JsonNode.class);
        return responseEntity.getStatusCode().equals(HttpStatus.OK);
    }

    private String getMapitQuotaEndpoint() {
        return mapitUrl + mapitQuotaPath;
    }
}
