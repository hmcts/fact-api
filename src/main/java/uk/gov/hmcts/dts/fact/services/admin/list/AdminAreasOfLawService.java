package uk.gov.hmcts.dts.fact.services.admin.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.repositories.AreasOfLawRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AdminAreasOfLawService {

    private final AreasOfLawRepository areasOfLawRepository;

    @Autowired
    public AdminAreasOfLawService(final AreasOfLawRepository areasOfLawRepository) {
        this.areasOfLawRepository = areasOfLawRepository;
    }

    public List<AreaOfLaw> getAllAreasOfLaw() {
        return areasOfLawRepository.findAll()
            .stream()
            .map(AreaOfLaw::new)
            .collect(toList());
    }
}
