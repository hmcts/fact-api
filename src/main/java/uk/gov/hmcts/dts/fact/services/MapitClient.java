package uk.gov.hmcts.dts.fact.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.hmcts.dts.fact.services.model.Coordinates;

@FeignClient(name = "mappitApi", url = "https://mapit.mysociety.org/")
public interface MapitClient {

    @GetMapping("/postcode/{postcode}")
    Coordinates getCoordinates(@PathVariable("postcode") String postcode);
}

