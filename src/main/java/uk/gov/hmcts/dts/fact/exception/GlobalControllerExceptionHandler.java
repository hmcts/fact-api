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

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<String> notFoundExceptionHandler(final NotFoundException ex) throws JsonProcessingException {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CONTENT_TYPE, APPLICATION_JSON);
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(error), responseHeaders, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPostcodeException.class)
    ResponseEntity invalidPostcodeExceptionHandler(final InvalidPostcodeException ex) throws JsonProcessingException {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CONTENT_TYPE, APPLICATION_JSON);
        if (ex.getInvalidPostcodes().isEmpty()) {
            error.put("message", ex.getMessage());
        } else {
            error.put("message", ex.getInvalidPostcodes().toString());
        }
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(error), responseHeaders, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> illegalArgumentExceptionHandler(final IllegalArgumentException ex) throws JsonProcessingException {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CONTENT_TYPE, APPLICATION_JSON);
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

    @ExceptionHandler(DuplicatedListItemException.class)
    ResponseEntity<String> duplicateListItemExceptionHandler(final DuplicatedListItemException ex) throws JsonProcessingException {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CONTENT_TYPE, APPLICATION_JSON);
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(error), responseHeaders, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalListItemException.class)
    ResponseEntity<String> illegalListItemExceptionHandler(final IllegalListItemException ex) throws JsonProcessingException {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CONTENT_TYPE, APPLICATION_JSON);
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(error), responseHeaders, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ListItemInUseException.class)
    ResponseEntity<String> listItemInUseExceptionHandler(final ListItemInUseException ex) throws JsonProcessingException {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(error), responseHeaders, HttpStatus.CONFLICT);
    }
}
