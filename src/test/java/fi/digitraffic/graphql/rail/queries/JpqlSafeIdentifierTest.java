package fi.digitraffic.graphql.rail.queries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import graphql.execution.AbortExecutionException;

/**
 * Unit tests for JpqlSafeIdentifier.
 */
class JpqlSafeIdentifierTest {

    @ParameterizedTest
    @ValueSource(strings = {"trainNumber", "name", "x", "train_number", "_field", "A", "camelCase123"})
    void acceptsValidIdentifiers(final String identifier) {
        assertEquals(identifier, JpqlSafeIdentifier.validate(identifier));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "field name",           // space
            "a; DROP TABLE",        // semicolon
            "func()",               // parentheses
            "a.b",                  // dot
            "1field",               // starts with digit
            "x OR 1=1 --",         // JPQL injection
            "name\nDROP",           // newline
            "field\ttab",           // tab
            "'quoted'",             // quotes
            "field-name",           // hyphen
    })
    void rejectsUnsafeIdentifiers(final String identifier) {
        assertThrows(AbortExecutionException.class, () -> JpqlSafeIdentifier.validate(identifier));
    }

    @Test
    void rejectsNull() {
        assertThrows(AbortExecutionException.class, () -> JpqlSafeIdentifier.validate(null));
    }

    @Test
    void rejectsEmptyString() {
        assertThrows(AbortExecutionException.class, () -> JpqlSafeIdentifier.validate(""));
    }
}

