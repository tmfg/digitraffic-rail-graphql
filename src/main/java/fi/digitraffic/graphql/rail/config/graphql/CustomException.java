package fi.digitraffic.graphql.rail.config.graphql;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class CustomException extends RuntimeException implements GraphQLError {
    private final int errorCode;

    public CustomException(int errorCode, String errorMessage) {
        super(errorMessage);

        this.errorCode = errorCode;
    }

    @Override
    public Map<String, Object> getExtensions() {
        Map<String, Object> customAttributes = new LinkedHashMap<>();

        customAttributes.put("errorCode", this.errorCode);
        customAttributes.put("errorMessage", this.getMessage());

        return customAttributes;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorType getErrorType() {
        return null;
    }
}
