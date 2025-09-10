// TODO Implement native arrays, increment operators, x= operators

// Semantics
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interpreter.java
 *
 * Implements the interpreter for the language, evaluating statements and expressions.
 * Manages environments, executes statements, and handles runtime errors.
 */
class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    /**
     * The global environment (contains built-in functions and global variables).
     */
    final Environment globals = new Environment();

    /**
     * The current environment (may be nested for blocks/functions).
     */
    private Environment environment = globals;

    /**
     * Map of expressions to their resolved scope depth (for variable resolution).
     */
    private final Map<Expr, Integer> locals = new HashMap<>();

    /**
     * Constructs a new Interpreter and defines built-in functions.
     * Postconditions: The global environment is initialized.
     */
    Interpreter() {
        // Define the built-in 'clock' function in the global environment.
        globals.define("clock", new KwiiCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double)System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
    }

    /**
     * Interprets a list of statements (the program AST).
     * @param statements The list of statements to execute.
     * Handles runtime errors and reports them.
     */
    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Kwii.runtimeError(error);
        }
    }


    /**
     * Visits a literal expression node and returns its value.
     * @param expr The literal expression node.
     * @return The literal value (may be null).
     */
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }


    /**
     * Visits a unary expression node and evaluates it.
     * @param expr The unary expression node (operator, right operand).
     * @return The result of the unary operation.
     */
    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        // Handle unary operators: - (negation), ! (logical not)
        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
            case BANG:
                return !isTrue(right);
        }

        return null; // Unreachable, but required for compilation
    }


    /**
     * Visits a variable expression node and retrieves its value from the environment.
     * @param expr The variable expression node.
     * @return The value of the variable.
     */
    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }


    /**
     * Looks up the value of a variable, using resolved scope distance if available.
     * @param name The variable name token.
     * @param expr The variable expression node.
     * @return The value of the variable.
     */
    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            // Variable is in a local (non-global) scope.
            return environment.getAt(distance, name.lexeme);
        } else {
            // Variable is global.
            return globals.get(name);
        }
    }


    /**
     * Checks that the operand is a number (Double), throws if not.
     * @param operator The operator token (for error reporting).
     * @param operand The operand to check.
     */
    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeException("Operand must be a number: " + operator.lexeme);
    }


    /**
     * Checks that both operands are numbers (Double), throws if not.
     * @param operator The operator token (for error reporting).
     * @param left The left operand.
     * @param right The right operand.
     */
    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeException("Operands must be numbers: " + operator.lexeme);
    }


    /**
     * Determines the truthiness of an object (for logical operations).
     * @param object The object to check.
     * @return True if the object is considered true, false otherwise.
     */
    private boolean isTrue(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true; // All other objects are considered true
    }


    /**
     * Visits a grouping expression node and evaluates the inner expression.
     * @param expr The grouping expression node.
     * @return The value of the inner expression.
     */
    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }


    /**
     * Evaluates an expression node by accepting this visitor.
     * @param expression The expression to evaluate.
     * @return The result of evaluating the expression.
     */
    private Object evaluate(Expr expression) {
        return expression.accept(this);
    }


    /**
     * Executes a statement node by accepting this visitor.
     * @param statement The statement to execute.
     */
    private void execute(Stmt statement) {
        statement.accept(this);
    }


    /**
     * Records the resolved scope depth for a variable expression.
     * @param expr The variable expression.
     * @param depth The scope depth.
     */
    public void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }


    /**
     * Visits a logical expression node (and/or) and evaluates it.
     * @param expr The logical expression node.
     * @return The result of the logical operation.
     */
    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);

        // Short-circuit evaluation for 'or' and 'and'.
        if (expr.operator.type == TokenType.OR) {
            if (isTrue(left)) return left;
        } else {
            if (!isTrue(left)) return left;
        }

        return evaluate(expr.right);
    }


    /**
     * Visits a set expression node (object property assignment) and evaluates it.
     * @param expr The set expression node.
     * @return The value assigned.
     */
    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object object = evaluate(expr.object);
        if (!(object instanceof KwiiInstance)) {
            throw new RuntimeError(expr.name, "Only instances have fields.");
        }

        Object value = evaluate(expr.value);
        ((KwiiInstance)object).set(expr.name, value);
        return value;
    }


    /**
     * Visits a super expression node (superclass method access) and evaluates it.
     * @param expr The super expression node.
     * @return The bound method from the superclass.
     */
    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        int distance = locals.get(expr);
        KwiiClass superclass = (KwiiClass)environment.getAt(distance, "super");

        KwiiInstance object = (KwiiInstance)environment.getAt(distance - 1, "this");
        KwiiFunction method = superclass.findMethod(expr.method.lexeme);

        if (method == null) {
            throw new RuntimeError(expr.method, "Undefined property '" + expr.method.lexeme + "'.");
        }

        return method.bind(object);
    }


    /**
     * Visits a this expression node (current instance reference) and evaluates it.
     * @param expr The this expression node.
     * @return The value of 'this' in the current environment.
     */
    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.keyword, expr);
    }


    /**
     * Executes a block of statements in a new environment (scope).
     * @param statements The list of statements to execute.
     * @param environment The environment (scope) to use for the block.
     * Postconditions: The previous environment is restored after execution.
     */
    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }


    /**
     * Visits an if statement node and executes the appropriate branch.
     * @param stmt The if statement node.
     * @return null (statements do not return values).
     */
    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTrue(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }


    /**
     * Visits a block statement node and executes its statements in a new environment.
     * @param stmt The block statement node.
     * @return null (statements do not return values).
     */
    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }


    /**
     * Visits a class statement node and defines the class in the environment.
     * Handles inheritance and method definitions.
     * @param stmt The class statement node.
     * @return null (statements do not return values).
     */
    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        Object superclass = null;
        if (stmt.superclass != null) {
            superclass = evaluate(stmt.superclass);
            if (!(superclass instanceof KwiiClass)) {
                throw new RuntimeError(stmt.superclass.name, "Superclass must be a class.");
            }
        }

        // Define the class name in the current environment (with null value for now).
        environment.define(stmt.name.lexeme, null);

        // If there is a superclass, create a new environment for 'super'.
        if (stmt.superclass != null) {
            environment = new Environment(environment);
            environment.define("super", superclass);
        }
        
        // Collect all methods for the class.
        Map<String, KwiiFunction> methods = new HashMap<>();
        for (Stmt.Function method : stmt.methods) {
            KwiiFunction function = new KwiiFunction(method, environment, method.name.lexeme.equals("init"));
            methods.put(method.name.lexeme, function);
        }

        // Create the class object.
        KwiiClass classObj = new KwiiClass(stmt.name.lexeme, (KwiiClass)superclass, methods);

        // Restore the previous environment if there was a superclass.
        if (superclass != null) {
            environment = environment.enclosing;
        }
        // Assign the class object to its name in the environment.
        environment.assign(stmt.name, classObj);
        return null;
    }


    /**
     * Visits an expression statement node and evaluates the expression.
     * @param stmt The expression statement node.
     * @return null (statements do not return values).
     */
    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }


    /**
     * Visits a function statement node and defines the function in the environment.
     * @param stmt The function statement node.
     * @return null (statements do not return values).
     */
    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        KwiiFunction function = new KwiiFunction(stmt, environment, false);
        environment.define(stmt.name.lexeme, function);
        return null;
    }


    /**
     * Visits a print statement node and prints the evaluated expression.
     * @param stmt The print statement node.
     * @return null (statements do not return values).
     */
    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }


    /**
     * Visits a return statement node and throws a Return exception to exit the function.
     * @param stmt The return statement node.
     * @return Never returns (throws Return exception).
     */
    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) value = evaluate(stmt.value);
        throw new Return(value);
    }


    /**
     * Visits a variable declaration statement node and defines the variable in the environment.
     * @param stmt The variable declaration statement node.
     * @return null (statements do not return values).
     */
    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }
        environment.define(stmt.name.lexeme, value);
        return null;
    }


    /**
     * Visits an assignment expression node and assigns the value to the variable.
     * @param expr The assignment expression node.
     * @return The value assigned.
     */
    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);

        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }

        return value;
    }


    /**
     * Visits a while statement node and executes the loop body while the condition is true.
     * @param stmt The while statement node.
     * @return null (statements do not return values).
     */
    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTrue(evaluate(stmt.condition))) {
            execute(stmt.body);
        }
        return null;
    }


    /**
     * Visits a binary expression node and evaluates the operation.
     * Handles arithmetic, comparison, and equality operators.
     * @param expr The binary expression node.
     * @return The result of the binary operation.
     */
    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        // Handle all binary operators.
        switch (expr.operator.type) {
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                if ((double)right == 0) {
                    throw new RuntimeError(expr.operator, "Division by zero.");
                }
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }
                // Allow string concatenation.
                if ((left instanceof String || right instanceof Double) && (left instanceof Double || right instanceof String)) {
                    try {
                        return stringify(left) + stringify(right);
                    } catch (Exception e) {
                        throw new RuntimeError(expr.operator, "Operands must be either strings or numbers.");
                    }
                }
                throw new RuntimeException("Operands must be two numbers or two strings: " + expr.operator.lexeme);
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
        }

        return null; // Unreachable, but required for compilation
    }


    /**
     * Visits a call expression node and evaluates the function or class call.
     * @param expr The call expression node.
     * @return The result of the function or class call.
     */
    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof KwiiCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }

        KwiiCallable function = (KwiiCallable)callee;
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + "instead.");
        }

        return function.call(this, arguments);
    }


    /**
     * Visits a get expression node (object property access) and evaluates it.
     * @param expr The get expression node.
     * @return The value of the property.
     */
    @Override 
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if (object instanceof KwiiInstance) {
            return ((KwiiInstance) object).get(expr.name);
        }
        throw new RuntimeError(expr.name, "Only instances have properties.");
    }


    /**
     * Checks if two objects are equal (used for equality operators).
     * @param left The left operand.
     * @param right The right operand.
     * @return True if equal, false otherwise.
     */
    private boolean isEqual(Object left, Object right) {
        if (left == null && right == null) return true;
        if (left == null || right == null) return false;
        return left.equals(right);
    }

    /**
     * Converts an object to its string representation for printing.
     * Handles null and removes trailing .0 from doubles.
     * @param object The object to stringify.
     * @return The string representation.
     */
    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                return text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }
}
