package uk.gov.hmcts.dts.fact.util;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("PMD.JUnit5TestShouldBePackagePrivate")
public class AdminFunctionalTestBase extends FunctionalTestBase {

    @Autowired
    private OAuthClient authClient;

    protected String authenticatedToken;
    protected String forbiddenToken;
    protected String superAdminToken;
    protected String viewerToken;

    @BeforeEach
    void setUpAuthenticationTokens() {
        authenticatedToken = authClient.getToken();
        forbiddenToken = authClient.getNobodyToken();
        superAdminToken = authClient.getSuperAdminToken();
        viewerToken = authClient.getViewerToken();
    }
}
