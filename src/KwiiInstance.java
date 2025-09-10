import java.util.HashMap;
import java.util.Map;

public class KwiiInstance {
    private KwiiClass classObj;
    private final Map<String, Object> fields = new HashMap<>();

    KwiiInstance(KwiiClass classObj) {
        this.classObj = classObj;
    }

    public Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        KwiiFunction method = classObj.findMethod(name.lexeme);
        if (method != null) return method.bind(this);

        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    public void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return classObj.name + " instance";
    }
}
