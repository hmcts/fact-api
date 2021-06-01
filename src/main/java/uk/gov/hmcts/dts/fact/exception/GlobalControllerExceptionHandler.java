package uk.gov.hmcts.dts.fact.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<String> notFoundExceptionHandler(final NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
        InvalidPostcodeException.class,
        IllegalArgumentException.class
    })
    ResponseEntity<String> invalidPostcodeExceptionHandler(final Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
