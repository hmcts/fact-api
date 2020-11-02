package uk.gov.hmcts.dts.fact.config.security;

import java.util.List;

public interface RolesProvider {

    List<String> getRoles();

}
