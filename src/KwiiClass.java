import java.util.List;
import java.util.Map;

/**
 * Represents a class in the Kwii interpreter.
 * Handles class instantiation, method lookup, and string representation.
 * Implements KwiiCallable so that classes can be called to create instances.
 */
public class KwiiClass implements KwiiCallable {
     /** The name of the class. */
    final String name;
     /** The superclass of this class, or null if none. Used for inheritance. */
    final KwiiClass superclass;
     /** Map of method names to KwiiFunction objects representing class methods. */
    private final Map<String, KwiiFunction> methods;

     /**
      * Constructs a new KwiiClass.
      *
      * @param name      The name of the class.
      * @param superclass The superclass (may be null).
      * @param methods   The map of method names to KwiiFunction objects.
      * Preconditions: name != null, methods != null
      * Postconditions: A new KwiiClass is created with the given properties.
      */
    KwiiClass(String name, KwiiClass superclass, Map<String, KwiiFunction> methods) {
        this.name = name;
        this.superclass = superclass;
        this.methods = methods;
    }

     /**
      * Looks up a method by name in this class or its superclasses.
      *
      * @param name The name of the method to find.
      * @return The KwiiFunction if found, or null if not found.
      * Preconditions: name != null
      * Postconditions: Returns the method or null.
      */
    KwiiFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (superclass != null) {
            return superclass.findMethod(name);
        }

        return null; // Method not found
    }

     /**
      * Returns a string representation of the class (its name).
      *
      * @return The class name as a string.
      */
    @Override
    public String toString() {
        return name;
    }

     /**
      * Calls the class to create a new instance, optionally invoking the initializer.
      *
      * @param interpreter The interpreter instance.
      * @param arguments   The arguments to pass to the initializer.
      * @return The new KwiiInstance.
      * Preconditions: arguments.size() == arity()
      * Postconditions: Returns a new instance, possibly initialized.
      */
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        KwiiInstance instance = new KwiiInstance(this);
        KwiiFunction initializer = findMethod("init");

        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }

        return instance;
    }

     /**
      * Returns the number of arguments expected by the class's initializer, if any.
      *
      * @return The arity of the initializer, or 0 if none.
      * Preconditions: None.
      * Postconditions: Returns a non-negative integer.
      */
    @Override
    public int arity() {
        KwiiFunction initializer = findMethod("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }
}
