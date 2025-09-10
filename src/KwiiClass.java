import java.util.List;
import java.util.Map;

public class KwiiClass implements KwiiCallable {
    final String name;
    final KwiiClass superclass;
    private final Map<String, KwiiFunction> methods;

    KwiiClass(String name, KwiiClass superclass, Map<String, KwiiFunction> methods) {
        this.name = name;
        this.superclass = superclass;
        this.methods = methods;
    }

    KwiiFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (superclass != null) {
            return superclass.findMethod(name);
        }

        return null; // Method not found
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        KwiiInstance instance = new KwiiInstance(this);
        KwiiFunction initializer = findMethod("init");

        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }

        return instance;
    }

    @Override
    public int arity() {
        KwiiFunction initializer = findMethod("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }
}
