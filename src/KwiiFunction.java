/**
 * Represents a function in the Kwii interpreter.
 * Handles function declaration, closure, binding, and invocation.
 * Implements KwiiCallable so that functions can be called as first-class objects.
 * Maintains closure for lexical scoping and supports method binding for classes.
 */
import java.util.List;

public class KwiiFunction implements KwiiCallable {
    /**
     * The function declaration AST node (Stmt.Function) that defines the function's parameters and body.
     * Immutable after construction.
     */
    private final Stmt.Function declaration;

    /**
     * The closure environment where the function was declared.
     * Used to capture lexical scope for closures and method binding.
     * Immutable after construction.
     */
    private final Environment closure;

    /**
     * True if this function is an initializer (constructor for a class), false otherwise.
     * Determines return semantics for 'init' methods.
     */
    private boolean isInitializer = false;

    /**
     * Constructs a new KwiiFunction.
     *
     * @param declaration   The function declaration AST node (must not be null).
     * @param closure       The closure environment (must not be null).
     * @param isInitializer True if this is a class initializer (constructor).
     * Preconditions: declaration != null, closure != null
     * Postconditions: A new KwiiFunction is created with the given properties.
     */
    KwiiFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
    }

    /**
     * Binds this function to a specific instance (for methods).
     * Used to set the 'this' context for method calls.
     *
     * @param instance The instance to bind to (must not be null).
     * @return A new KwiiFunction bound to the given instance.
     * Preconditions: instance != null
     * Postconditions: Returns a new KwiiFunction with 'this' set in its closure.
     */
    KwiiFunction bind(KwiiInstance instance) {
        // Create a new environment that encloses the current closure
        Environment environment = new Environment(closure);
        // Define 'this' in the new environment to refer to the given instance
        environment.define("this", instance);
        // Return a new KwiiFunction with the new environment as its closure
        return new KwiiFunction(declaration, environment, isInitializer);
    }

    /**
     * Returns a string representation of the function, including its name.
     *
     * @return The function name in the format <fn name>.
     * Preconditions: None.
     * Postconditions: Returns a non-null string.
     */
    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }

    /**
     * Returns the number of arguments expected by this function.
     *
     * @return The arity (number of parameters).
     * Preconditions: None.
     * Postconditions: Returns a non-negative integer.
     */
    @Override
    public int arity() {
        return declaration.params.size();
    }

    /**
     * Calls the function with the given arguments in the interpreter context.
     * Sets up a new environment for the function's scope, binds parameters, and executes the body.
     * Handles return values and initializer semantics.
     *
     * @param interpreter The interpreter instance executing the call (must not be null).
     * @param arguments   The arguments to pass to the function (must match arity).
     * @return The result of the function call, or the instance for initializers, or null if no return.
     * Preconditions: arguments.size() == arity()
     * Postconditions: Returns the function's return value, or the instance for initializers, or null.
     */
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        // Create a new environment for the function's execution, enclosing the closure
        Environment environment = new Environment(closure);
        // Bind each parameter to its corresponding argument
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        try {
            // Execute the function body in the new environment
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            // If this is an initializer, always return 'this' from the closure
            if (isInitializer) {
                return closure.getAt(0, "this");
            }
            // Otherwise, return the value from the return statement
            return returnValue.value;
        }

        // If this is an initializer and no explicit return, return 'this'
        if (isInitializer) return closure.getAt(0, "this");

        // Default return value if no return statement is executed
        return null;
    }
}
