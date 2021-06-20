package uk.gov.hmcts.dts.fact.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.ResponseEntity.badRequest;

@ControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<String> notFoundExceptionHandler(final NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPostcodeException.class)
    ResponseEntity invalidPostcodeExceptionHandler(final InvalidPostcodeException ex) {
        if (ex.getInvalidPostcodes().isEmpty()) {
            return badRequest().body(ex.getMessage());
        }
        return badRequest().body(ex.getInvalidPostcodes());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> illegalArgumentExceptionHandler(final IllegalArgumentException ex) {
        return badRequest().body(ex.getMessage());
    }
}
