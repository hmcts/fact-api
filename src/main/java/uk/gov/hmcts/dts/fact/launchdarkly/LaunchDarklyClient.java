package uk.gov.hmcts.dts.fact.launchdarkly;

import com.launchdarkly.sdk.LDContext;
import com.launchdarkly.sdk.server.interfaces.LDClientInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The LaunchDarklyClient class.
 */
@Service
public class LaunchDarklyClient {
    public final LDContext factApiContext;

    private final LDClientInterface internalClient;

    /**
     * Constructor for the LaunchDarklyClient.
     *
     * @param launchDarklyClientFactory the factory to create the client
     * @param sdkKey the SDK key
     * @param offlineMode whether the client should be in offline mode
     */
    @Autowired
    public LaunchDarklyClient(
        LaunchDarklyClientFactory launchDarklyClientFactory,
        @Value("${launchdarkly.sdk-key}") String sdkKey,
        @Value("${launchdarkly.offline-mode:false}") Boolean offlineMode
    ) {

        this.internalClient = launchDarklyClientFactory.create(sdkKey, offlineMode);
        this.factApiContext =  LDContext.builder(sdkKey).build();
    }

    /**
     * Checks if a feature is enabled.
     *
     * @param feature the feature to check
     * @return true if the feature is enabled, false otherwise
     */
    public boolean isFeatureEnabled(String feature) {
        return internalClient.boolVariation(feature, factApiContext, false);
    }

    /**
     * Checks if a feature is enabled for a specific user.
     *
     * @param feature the feature to check
     * @param user the user to check the feature for
     * @return true if the feature is enabled, false otherwise
     */
    public boolean isFeatureEnabled(String feature, LDContext user) {
        return internalClient.boolVariation(feature, user, false);
    }
}
