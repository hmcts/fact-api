package uk.gov.hmcts.dts.fact.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.dts.fact.config.AzureMapsFeignConfiguration;
import uk.gov.hmcts.dts.fact.model.RouteMatrixRequest;
import uk.gov.hmcts.dts.fact.model.RouteMatrixResponse;

@FeignClient(
    name = "azureMapsRouteMatrixClient",
    url = "https://atlas.microsoft.com",
    configuration = AzureMapsFeignConfiguration.class
)
public interface AzureMapsRouteMatrixClient {

    @PostMapping(
        value = "/route/matrix",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    RouteMatrixResponse calculateMatrix(
        @RequestParam("api-version") String apiVersion,
        @RequestBody RouteMatrixRequest body
    );
}

