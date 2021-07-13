package uk.gov.hmcts.dts.fact.mapit;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.LinkedHashMap;

@FeignClient(name = "mappitApi", url = "${mapit.url}")
public interface MapitClient {

    @GetMapping("${mapit.endpoint.postcode-search}/{postcode}")
    MapitData getMapitData(@PathVariable("postcode") String postcode);

    @GetMapping("${mapit.endpoint.postcode-search}/partial/{postcode}")
    MapitData getMapitDataWithPartial(@PathVariable("postcode") String postcode);

    // This can be narrowed to specific types using 'type' query parameter in future e.g. ?types=MTD,MTW
    // https://mapit.mysociety.org/docs/#api-multiple_areas
    @GetMapping("${mapit.endpoint.area-search}/{area}")
    LinkedHashMap<String, MapitArea> getMapitDataForLocalAuthorities(@PathVariable("area") String namePrefix);
}
