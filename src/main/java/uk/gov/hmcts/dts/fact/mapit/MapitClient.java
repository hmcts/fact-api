package uk.gov.hmcts.dts.fact.mapit;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.hmcts.dts.fact.config.appinsights.DependencyProfiler;

@FeignClient(name = "mappitApi", url = "https://mapit.mysociety.org/")
public interface MapitClient {

    @DependencyProfiler(name = "mapit", action = "postcode")
    @GetMapping("/postcode/{postcode}")
    Coordinates getCoordinates(@PathVariable("postcode") String postcode);
}

