package uk.gov.hmcts.dts.fact.launchdarkly;

import com.launchdarkly.sdk.server.LDClient;
import com.launchdarkly.sdk.server.LDConfig;
import com.launchdarkly.sdk.server.interfaces.LDClientInterface;
import org.springframework.stereotype.Service;

/**
 * The LaunchDarklyClientFactory class.
 */
@Service
public class LaunchDarklyClientFactory {

    /**
     * Creates a new LaunchDarkly client.
     *
     * @param sdkKey the SDK key
     * @param offlineMode whether the client should be in offline mode
     * @return the new client
     */
    public LDClientInterface create(String sdkKey, boolean offlineMode) {
        LDConfig config = new LDConfig.Builder()
            .offline(offlineMode)
            .build();
        return new LDClient(sdkKey, config);
    }
}
