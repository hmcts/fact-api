package uk.gov.hmcts.dts.fact.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    private static final String MESSAGE = "message";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    /**
     * Handles NotFoundException.
     *
     * @param ex the exception
     * @return the response entity
     * @throws JsonProcessingException if the response entity cannot be converted to a string
     */
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<String> notFoundExceptionHandler(final NotFoundException ex) throws JsonProcessingException {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CONTENT_TYPE, APPLICATION_JSON);
        error.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(error), responseHeaders, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles InvalidPostcodeException.
     *
     * @param ex the exception
     * @return the response entity
     * @throws JsonProcessingException if the response entity cannot be converted to a string
     */
    @ExceptionHandler(InvalidPostcodeException.class)
    ResponseEntity<String> invalidPostcodeExceptionHandler(final InvalidPostcodeException ex) throws JsonProcessingException {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CONTENT_TYPE, APPLICATION_JSON);
        if (ex.getInvalidPostcodes().isEmpty()) {
            error.put(MESSAGE, ex.getMessage());
        } else {
            error.put(MESSAGE, String.join(",",ex.getInvalidPostcodes()));
        }
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(error), responseHeaders, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles LockExistsException.
     *
     * @param ex the exception
     * @return the response entity
     * @throws JsonProcessingException if the response entity cannot be converted to a string
     */
    @ExceptionHandler(LockExistsException.class)
    ResponseEntity lockExistsExceptionHandler(final LockExistsException ex) throws JsonProcessingException {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        log.error("Lock is currently in use exception: {}", ex.getMessage());
        responseHeaders.set(CONTENT_TYPE, APPLICATION_JSON);
        error.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(error), responseHeaders, HttpStatus.CONFLICT);
    }

    /**
     * Handles IllegalArgumentException.
     *
     * @param ex the exception
     * @return the response entity
     * @throws JsonProcessingException if the response entity cannot be converted to a string
     */
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> illegalArgumentExceptionHandler(final IllegalArgumentException ex) throws JsonProcessingException {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CONTENT_TYPE, APPLICATION_JSON);
        error.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(error), responseHeaders, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles PostcodeExistedException.
     *
     * @param ex the exception
     * @return the response entity
     */
    @ExceptionHandler(PostcodeExistedException.class)
    ResponseEntity<List<String>> postcodeExistedExceptionHandler(final PostcodeExistedException ex) {
        return new ResponseEntity<>(ex.getInvalidPostcodes(), HttpStatus.CONFLICT);
    }

    /**
     * Handles PostcodeNotFoundException.
     *
     * @param ex the exception
     * @return the response entity
     */
    @ExceptionHandler(PostcodeNotFoundException.class)
    ResponseEntity<List<String>> postcodeNotFoundExceptionHandler(final PostcodeNotFoundException ex) {
        return new ResponseEntity<>(ex.getInvalidPostcodes(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles DuplicatedListItemException.
     *
     * @param ex the exception
     * @return the response entity
     * @throws JsonProcessingException if the response entity cannot be converted to a string
     */
    @ExceptionHandler(DuplicatedListItemException.class)
    ResponseEntity<String> duplicateListItemExceptionHandler(final DuplicatedListItemException ex) throws JsonProcessingException {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CONTENT_TYPE, APPLICATION_JSON);
        error.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(error), responseHeaders, HttpStatus.CONFLICT);
    }

    /**
     * Handles IllegalListItemException.
     *
     * @param ex the exception
     * @return the response entity
     * @throws JsonProcessingException if the response entity cannot be converted to a string
     */
    @ExceptionHandler(IllegalListItemException.class)
    ResponseEntity<String> illegalListItemExceptionHandler(final IllegalListItemException ex) throws JsonProcessingException {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CONTENT_TYPE, APPLICATION_JSON);
        error.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(error), responseHeaders, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ListItemInUseException.
     *
     * @param ex the exception
     * @return the response entity
     * @throws JsonProcessingException if the response entity cannot be converted to a string
     */
    @ExceptionHandler(ListItemInUseException.class)
    ResponseEntity<String> listItemInUseExceptionHandler(final ListItemInUseException ex) throws JsonProcessingException {
        HashMap<String, String> error = new HashMap<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CONTENT_TYPE, APPLICATION_JSON);
        error.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(error), responseHeaders, HttpStatus.CONFLICT);
    }
}
