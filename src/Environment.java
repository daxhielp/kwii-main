import java.util.HashMap;
import java.util.Map;

/**
 * Environment.java
 *
 * Represents a variable environment for the interpreter, supporting lexical scoping.
 * Stores variable bindings and supports nested environments for block scoping.
 */
public class Environment {
    /**
     * The enclosing (parent) environment. Null if this is the global/root environment.
     * Used for lexical scoping.
     */
    public final Environment enclosing;

    /**
     * Map of variable names to their values in this environment.
     */
    private final Map<String, Object> values = new HashMap<>();

    /**
     * Constructs a new global/root environment (no enclosing environment).
     * Postcondition: enclosing == null
     */
    public Environment() {
        enclosing = null; // Root environment
    }

    /**
     * Constructs a new environment nested within an enclosing environment.
     * @param enclosing The parent environment (must not be null for nested scopes).
     * Postcondition: this.enclosing == enclosing
     */
    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    /**
     * Retrieves the value of a variable by name, searching this environment and enclosing ones.
     * @param name The token representing the variable name.
     * @return The value of the variable if found.
     * @throws RuntimeError if the variable is not defined in any accessible scope.
     * Logic: Checks this environment first, then recursively checks enclosing environments.
     */
    public Object get(Token name) {
        // Check if the variable exists in the current environment.
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        // If not found, check the enclosing environment (if any).
        if (enclosing != null) {
            return enclosing.get(name);
        }

        // Variable not found in any scope: throw an error.
        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    /**
     * Assigns a value to an existing variable, searching this and enclosing environments.
     * @param name The token representing the variable name.
     * @param value The value to assign.
     * @throws RuntimeError if the variable is not defined in any accessible scope.
     * Logic: Assigns in the first environment where the variable is found.
     */
    public void assign(Token name, Object value) {
        // If the variable exists in this environment, assign it here.
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        // Otherwise, try to assign in the enclosing environment.
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        // Variable not found in any scope: throw an error.
        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    /**
     * Defines a new variable in the current environment.
     * @param name The variable name.
     * @param value The value to assign.
     * Logic: Always creates/overwrites in this environment only.
     */
    public void define(String name, Object value) {
        values.put(name, value);
    }

    /**
     * Returns the ancestor environment at a given distance up the chain.
     * @param distance How many environments to go up (0 = this, 1 = parent, etc.).
     * @return The ancestor environment.
     * Logic: Follows the enclosing chain for 'distance' steps.
     */
    private Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }
        return environment;
    }

    /**
     * Retrieves the value of a variable at a specific distance up the environment chain.
     * @param distance How many environments to go up.
     * @param name The variable name.
     * @return The value of the variable at the ancestor environment.
     */
    public Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    /**
     * Assigns a value to a variable at a specific distance up the environment chain.
     * @param distance How many environments to go up.
     * @param name The token representing the variable name.
     * @param value The value to assign.
     * Logic: Assigns in the ancestor environment at the given distance.
     */
    public void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }
}
