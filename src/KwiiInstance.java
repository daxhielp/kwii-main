import java.util.HashMap;
import java.util.Map;

/**
 * Represents an instance of a KwiiClass in the interpreter.
 * Handles field storage, method lookup, and property access.
 */
public class KwiiInstance {
     /** The class of which this is an instance. */
    private KwiiClass classObj;
     /** Map of field names to their values for this instance. */
    private final Map<String, Object> fields = new HashMap<>();

     /**
      * Constructs a new instance of the given class.
      *
      * @param classObj The class of the instance.
      * Preconditions: classObj != null
      * Postconditions: A new instance is created.
      */
    KwiiInstance(KwiiClass classObj) {
        this.classObj = classObj;
    }

     /**
      * Gets the value of a property (field or method) by name.
      *
      * @param name The token representing the property name.
      * @return The value of the field or a bound method.
      * @throws RuntimeError if the property does not exist.
      * Preconditions: name != null
      * Postconditions: Returns the property value or throws.
      */
    public Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        KwiiFunction method = classObj.findMethod(name.lexeme);
        if (method != null) return method.bind(this);

        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

     /**
      * Sets the value of a field by name.
      *
      * @param name  The token representing the field name.
      * @param value The value to set.
      * Preconditions: name != null
      * Postconditions: The field is set to the given value.
      */
    public void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

     /**
      * Returns a string representation of the instance (its class name and 'instance').
      *
      * @return The string representation.
      */
    @Override
    public String toString() {
        return classObj.name + " instance";
    }
}
