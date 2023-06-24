package vez.common;

/**
 * Define a Java {@code record} that holds the "plain old data" (POD) for the result of execution.
 */
public record Result(
        String op, // Operation performed.
        boolean success // Result of the operation
) { }
