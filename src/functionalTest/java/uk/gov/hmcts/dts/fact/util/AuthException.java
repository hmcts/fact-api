package uk.gov.hmcts.dts.fact.util;

public class AuthException extends RuntimeException {
    public static final long serialVersionUID = 42L;

    public AuthException(String error) {
        super(error);
    }
}
