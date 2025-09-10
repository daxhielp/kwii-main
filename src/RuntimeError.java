/**
 * RuntimeError.java
 *
 * Represents a runtime error encountered during interpretation.
 * Extends RuntimeException to allow for error propagation and handling.
 */
public class RuntimeError extends RuntimeException {
    /**
     * The token where the error occurred (for error reporting).
     */
    final Token token;

    /**
     * Constructs a new RuntimeError instance.
     * @param token The token at which the error occurred.
     * @param message The error message to display.
     * Preconditions: token and message must not be null.
     * Postconditions: The error is initialized and can be thrown/caught.
     */
    public RuntimeError(Token token, String message) {
        super(message); // Call the superclass constructor with the error message.
        this.token = token;
    }
}
