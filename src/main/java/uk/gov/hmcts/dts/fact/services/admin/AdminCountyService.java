package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.model.admin.County;
import uk.gov.hmcts.dts.fact.repositories.CountyRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Service for admin county data.
 */
@Service
public class AdminCountyService {
    private final CountyRepository countyRepository;

    /**
     * Constructor for the AdminCountyService.
     * @param countyRepository The repository for county
     */
    public AdminCountyService(CountyRepository countyRepository) {
        this.countyRepository = countyRepository;
    }

    /**
     * Get all counties.
     * @return A list of counties
     */
    public List<County> getAllCounties() {
        return countyRepository.findAll()
            .stream()
            .map(County::new)
            .sorted(Comparator.comparing(County::getName))
            .collect(toList());
    }

    /**
     * Get a map of all counties.
     * @return A map of counties
     */
    public Map<Integer, uk.gov.hmcts.dts.fact.entity.County> getCountyMap() {
        return countyRepository.findAll()
            .stream()
            .collect(toMap(uk.gov.hmcts.dts.fact.entity.County::getId, county -> county));
    }
}
