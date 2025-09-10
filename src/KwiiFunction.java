import java.util.List;

public class KwiiFunction implements KwiiCallable {
    private final Stmt.Function declaration;
    private final Environment closure;
    private boolean isInitializer = false;

    

    KwiiFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
    }

    KwiiFunction bind(KwiiInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new KwiiFunction(declaration, environment, isInitializer);
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            if (isInitializer) {
                return closure.getAt(0, "this");
            }
            // If a return statement is encountered, we return the value
            return returnValue.value;
        }

        if (isInitializer) return closure.getAt(0, "this");

        return null; // Default return value if no return statement is executed
    }
}
