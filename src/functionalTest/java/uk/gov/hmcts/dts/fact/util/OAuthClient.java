package uk.gov.hmcts.dts.fact.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
public class OAuthClient {

    @Value("${OAUTH_CLIENT_ID:fact_admin}")
    private String clientId;

    @Value("${OAUTH_SECRET:}")
    private String clientSecret;

    @Value("${OAUTH_PROVIDER_URL:https://idam-web-public.aat.platform.hmcts.net}")
    private String providerUrl;

    @Value("${OAUTH_USER}")
    private String username;

    @Value("${OAUTH_USER_PASSWORD}")
    private String password;

    @Value("${OAUTH_SUPER_USER}")
    private String superUser;

    @Value("${OAUTH_WRONG_ROLE_USER:hmcts.wrong.fact@gmail.com}")
    private String wrongRoleUser;

    @Value("${OAUTH_VIEWER_USER}")
    private String viewerUser;

    public String getToken() {
        return generateClientToken(username, password);
    }

    public String getSuperAdminToken() {
        return generateClientToken(superUser, password);
    }

    public String getNobodyToken() {
        return generateClientToken(wrongRoleUser, password);
    }

    public String getViewerToken() {
        return generateClientToken(viewerUser, password);
    }

    public String generateClientToken(String userName, String password) {
        String token = given()
            .relaxedHTTPSValidation()
            .baseUri(providerUrl)
            .post("/o/token?grant_type=password&username=" + userName + "&password=" + password
                      + "&client_id=" + clientId + "&client_secret=" + clientSecret + "&scope=openid profile roles")
            .body()
            .jsonPath()
            .get("id_token");

        if (token == null) {
            throw new AuthException(
                String.format(
                    "Unable to get token with %s %s %s",
                    providerUrl,
                    clientId,
                    clientSecret
                )
            );
        }

        return token;
    }
}
