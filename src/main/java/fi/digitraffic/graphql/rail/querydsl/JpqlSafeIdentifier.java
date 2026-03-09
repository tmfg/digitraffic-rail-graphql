package fi.digitraffic.graphql.rail.querydsl;

import java.util.regex.Pattern;

import graphql.execution.AbortExecutionException;

/**
 * Validates that field names used in dynamically built JPQL queries are safe identifiers.
 * <p>
 * Although GraphQL schema validation restricts the field names that can appear in
 * {@code where} and {@code orderBy} arguments, this provides a defence-in-depth safeguard
 * against JPQL injection in case the schema layer is ever bypassed or misconfigured.
 * <p>
 * A safe identifier matches the pattern {@code [a-zA-Z_][a-zA-Z0-9_]*} — letters, digits,
 * and underscores, starting with a letter or underscore.
 */
public final class JpqlSafeIdentifier {

    private static final Pattern SAFE_IDENTIFIER = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");

    private JpqlSafeIdentifier() {
    }

    /**
     * Validates that the given field name is a safe JPQL identifier.
     *
     * @param fieldName the field name to validate
     * @return the field name unchanged, if valid
     * @throws AbortExecutionException if the field name contains unsafe characters
     */
    public static String validate(final String fieldName) {
        if (fieldName == null || !SAFE_IDENTIFIER.matcher(fieldName).matches()) {
            throw new AbortExecutionException("Invalid field name: " + fieldName);
        }
        return fieldName;
    }
}

