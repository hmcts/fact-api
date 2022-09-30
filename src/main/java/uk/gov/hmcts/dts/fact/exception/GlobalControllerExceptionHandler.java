package uk.gov.hmcts.dts.fact.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;

import static org.springframework.http.ResponseEntity.badRequest;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(value = {NotFoundException.class})
    ResponseEntity<String> notFoundExceptionHandler(final NotFoundException ex) throws JsonProcessingException
    {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(error), responseHeaders, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPostcodeException.class)
    ResponseEntity invalidPostcodeExceptionHandler(final InvalidPostcodeException ex) {
        if (ex.getInvalidPostcodes().isEmpty()) {
            return badRequest().body(ex.getMessage());
        }
        return badRequest().body(ex.getInvalidPostcodes());
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    ResponseEntity<String> illegalArgumentExceptionHandler(final IllegalArgumentException ex) throws JsonProcessingException {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(error), responseHeaders, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostcodeExistedException.class)
    ResponseEntity<List<String>> postcodeExistedExceptionHandler(final PostcodeExistedException ex) {
        return new ResponseEntity<>(ex.getInvalidPostcodes(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PostcodeNotFoundException.class)
    ResponseEntity<List<String>> postcodeNotFoundExceptionHandler(final PostcodeNotFoundException ex) {
        return new ResponseEntity<>(ex.getInvalidPostcodes(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {DuplicatedListItemException.class})
    ResponseEntity<String> duplicateListItemExceptionHandler(final DuplicatedListItemException ex) throws JsonProcessingException {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(error), responseHeaders, HttpStatus.CONFLICT);
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
