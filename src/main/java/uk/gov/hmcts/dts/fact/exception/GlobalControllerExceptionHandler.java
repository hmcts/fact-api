package uk.gov.hmcts.dts.fact.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

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

    @ExceptionHandler(PostcodeExistedException.class)
    ResponseEntity<List<String>> postcodeExistedExceptionHandler(final PostcodeExistedException ex) {
        return new ResponseEntity<>(ex.getInvalidPostcodes(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PostcodeNotFoundException.class)
    ResponseEntity<List<String>> postcodeNotFoundExceptionHandler(final PostcodeNotFoundException ex) {
        return new ResponseEntity<>(ex.getInvalidPostcodes(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicatedListItemException.class)
    ResponseEntity<String> duplicateListItemExceptionHandler(final DuplicatedListItemException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalListItemException.class)
    ResponseEntity<String> illegalListItemExceptionHandler(final IllegalListItemException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ListItemInUseException.class)
    ResponseEntity<String> listItemInUseExceptionHandler(final ListItemInUseException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
}
