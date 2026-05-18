package uk.gov.hmcts.dts.fact.util;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("PMD.JUnit5TestShouldBePackagePrivate")
public class AdminFunctionalTestBase extends FunctionalTestBase {

    @Autowired
    private OAuthClient authClient;

    protected static String authenticatedToken;
    protected static String forbiddenToken;
    protected static String superAdminToken;
    protected static String viewerToken;

    private static final Object LOCK = new Object();

    @BeforeEach
    void setUpAuthenticationTokens() {
        synchronized (LOCK) {
            if (authenticatedToken == null) {
                authenticatedToken = authClient.getToken();
                forbiddenToken = authClient.getNobodyToken();
                superAdminToken = authClient.getSuperAdminToken();
                viewerToken = authClient.getViewerToken();
            }
        }
    }
}
