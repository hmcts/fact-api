package uk.gov.hmcts.dts.fact.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalControllerExceptionHandlerTest {

    private GlobalControllerExceptionHandler exceptionHandler;
    private ObjectMapper objectMapper;

    private static final String EPIM_ERROR = "invalid-epim-id!!";
    private static final String EPIM_ERROR_MESSAGE = "Invalid epimId: " + EPIM_ERROR;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalControllerExceptionHandler();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldReturnBadRequestResponseWithJsonErrorMessage() throws JsonProcessingException {

        InvalidEpimIdException exception = new InvalidEpimIdException(EPIM_ERROR);

        ResponseEntity<String> response = exceptionHandler.invalidEpimIdExceptionHandler(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> responseBody = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("message"));
        assertEquals(EPIM_ERROR_MESSAGE, responseBody.get("message"));
    }
}
