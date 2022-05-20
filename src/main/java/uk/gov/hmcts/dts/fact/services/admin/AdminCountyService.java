package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.model.admin.County;
import uk.gov.hmcts.dts.fact.repositories.CountyRepository;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class AdminCountyService {
    private final CountyRepository countyRepository;

    public AdminCountyService(CountyRepository countyRepository) {
        this.countyRepository = countyRepository;
    }

    public List<County> getAllCounties() {
        return countyRepository.findAll()
            .stream()
            .map(County::new)
            .collect(toList());
    }

    public Map<Integer, uk.gov.hmcts.dts.fact.entity.County> getCountyMap() {
        return countyRepository.findAll()
            .stream()
            .collect(toMap(uk.gov.hmcts.dts.fact.entity.County::getId, county -> county));
    }
}
