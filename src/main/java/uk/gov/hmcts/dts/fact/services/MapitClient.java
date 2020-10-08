package uk.gov.hmcts.dts.fact.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.gov.hmcts.dts.fact.services.model.Coordinates;

@FeignClient(name = "mappitApi", url = "https://mapit.mysociety.org/")
public interface MapitClient {

    @GetMapping( value = "/postcode/{postcode}")
    Coordinates getCoordinates(@PathVariable("postcode") String postcode);
}

