import java.util.List;

/**
 * Interface representing a callable entity in the Kwii interpreter.
 * This can be a function, class, or any object that can be invoked with arguments.
 * Implementations must define the arity (number of expected arguments) and the call logic.
 */
interface KwiiCallable {
     /**
      * Returns the number of arguments expected by this callable.
      *
      * @return The arity (number of parameters) required by the callable.
      * Preconditions: None.
      * Postconditions: Returns a non-negative integer.
      */
    int arity();
     /**
      * Invokes the callable entity with the given interpreter and arguments.
      *
      * @param interpreter The interpreter instance executing the call. Used for environment and context.
      * @param arguments   The list of arguments passed to the callable. Must match arity().
      * @return The result of the call, which may be any object or null.
      * @throws RuntimeError if the call fails due to runtime issues (e.g., wrong argument count).
      * Preconditions: arguments.size() == arity()
      * Postconditions: Returns the result of the callable's execution.
      */
    Object call(Interpreter interpreter, List<Object> arguments);
}
