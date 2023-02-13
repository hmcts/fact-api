package uk.gov.hmcts.dts.fact.util;

@SuppressWarnings("PMD.JUnit5TestShouldBePackagePrivate")
public class AuthException extends RuntimeException {
    public static final long serialVersionUID = 42L;

    public AuthException(String error) {
        super(error);
    }
}
