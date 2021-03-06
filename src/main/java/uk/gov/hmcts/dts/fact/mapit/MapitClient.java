package uk.gov.hmcts.dts.fact.mapit;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "mappitApi", url = "${mapit.url}")
public interface MapitClient {

    @GetMapping("${mapit.endpoint.postcode-search}/{postcode}")
    MapitData getMapitData(@PathVariable("postcode") String postcode);

    @GetMapping("${mapit.endpoint.postcode-search}/partial/{postcode}")
    MapitData getMapitDataWithPartial(@PathVariable("postcode") String postcode);

    @GetMapping("${mapit.endpoint.area-search}/{area}")
    Map<String, MapitArea> getMapitDataForLocalAuthorities(@PathVariable("area") String area, @RequestParam("type") String types);
}
