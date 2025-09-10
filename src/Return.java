/**
 * Return.java
 *
 * Special exception used to implement function return statements in the interpreter.
 * Carries the return value up the call stack.
 */
public class Return extends RuntimeException {
    /**
     * The value to return from the function (may be null for no return value).
     */
    final Object value;

    /**
     * Constructs a new Return exception with the given value.
     * @param value The value to return from the function.
     * Postconditions: The exception is initialized and can be thrown/caught.
     */
    public Return(Object value) {
        // Suppress stack trace and message for control flow.
        super(null, null, false, false);
        this.value = value;
    }
}
