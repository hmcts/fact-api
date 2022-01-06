package uk.gov.hmcts.dts.fact.html.sanitizer;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class OWASPHTMLSanitizer {
    public static String sanitizeHTML(String untrustedHTML) {
        PolicyFactory policy = new HtmlPolicyBuilder()
            .allowElements("a", "strong", "em")
            .allowUrlProtocols("https")
            .allowAttributes("href").onElements("a")
            .requireRelNofollowOnLinks()
            .toFactory();
        return policy.sanitize(untrustedHTML);
    }
}
