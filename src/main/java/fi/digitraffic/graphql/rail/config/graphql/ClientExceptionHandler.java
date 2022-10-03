package fi.digitraffic.graphql.rail.config.graphql;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import graphql.execution.NonNullableValueCoercedAsNullException;

@ControllerAdvice
public class ClientExceptionHandler {

    @ExceptionHandler(value = NonNullableValueCoercedAsNullException.class)
    public ResponseEntity<String> handleBadGraphqlRequests(
            NonNullableValueCoercedAsNullException e) {


        return new ResponseEntity<>(e.getMessage(),
                HttpStatus.BAD_REQUEST);
    }
}