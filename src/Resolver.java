import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


/**
 * The Resolver class performs static analysis on the AST to resolve variable scopes, function and class contexts, and detect errors such as variable shadowing or misuse of 'this' and 'super'.
 * It implements both Expr.Visitor<Void> and Stmt.Visitor<Void> to traverse the AST nodes.
 * The resolver maintains a stack of scopes to track variable declarations and definitions, and tracks the current function and class context for semantic checks.
 *
 * Responsibilities:
 * - Resolve variable and function declarations and usages.
 * - Track and enforce scoping rules for blocks, functions, and classes.
 * - Detect semantic errors such as returning from top-level code, using 'this' or 'super' incorrectly, and variable shadowing.
 * - Annotate the AST for the interpreter to enable efficient variable lookup.
 */
public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
     /** The interpreter instance to which resolution results are reported. */
    private final Interpreter interpreter;
     /** Stack of scopes, each a map from variable name to a boolean indicating if it's defined. */
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
     /** The type of function currently being resolved (NONE, FUNCTION, INITIALIZER, METHOD). */
    private FunctionType currentFunction = FunctionType.NONE;
     /** The type of class currently being resolved (NONE, CLASS, SUBCLASS). */
    
    Resolver(Interpreter interpreter) {
     /**
      * Constructs a Resolver for the given interpreter.
      * @param interpreter The interpreter instance (must not be null).
      * Preconditions: interpreter != null
      * Postconditions: A new Resolver is created with empty scope stack.
      */
        this.interpreter = interpreter;
    }

    enum FunctionType {
        NONE,
        FUNCTION,
        INITIALIZER,
        METHOD
    }

    enum ClassType {
        NONE,
        CLASS,
        SUBCLASS
    }

    private ClassType currentClass = ClassType.NONE;
    public void resolve(List<Stmt> statements) {
     /**
      * Resolves a list of statements (the program or a block).
      * @param statements The list of statements to resolve.
      * Preconditions: statements != null
      * Postconditions: All statements are resolved and scopes are updated accordingly.
      */
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    @Override
     /**
      * Visits and resolves a block statement, creating a new scope for its variables.
      * @param stmt The block statement.
      * Preconditions: stmt != null
      * Postconditions: All statements in the block are resolved in a new scope.
      */
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override
     /**
      * Visits and resolves a class statement, handling superclass, methods, and scoping for 'this' and 'super'.
      * @param stmt The class statement.
      * Preconditions: stmt != null
      * Postconditions: The class and its methods are resolved, and errors are reported for invalid inheritance.
      */
    public Void visitClassStmt(Stmt.Class stmt) {
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;

        declare(stmt.name);
        define(stmt.name);

        if (stmt.superclass != null && stmt.name.lexeme.equals(((Expr.Variable)stmt.superclass).name.lexeme)) {
            Kwii.error(stmt.superclass.name, "A class cannot inherit from itself.");
        }

        if (stmt.superclass != null) {
            currentClass = ClassType.SUBCLASS;
            resolve(stmt.superclass);
        }

        if (stmt.superclass != null) {
            beginScope();
            scopes.peek().put("super", true);
        }

        beginScope();
        scopes.peek().put("this", true);

        for (Stmt.Function method : stmt.methods) {
            FunctionType declaration = FunctionType.METHOD;
            if (method.name.lexeme.equals("init")) {
                declaration = FunctionType.INITIALIZER;
            }
            resolveFunction(method, declaration);
        }
        endScope();

        if (stmt.superclass != null) endScope();

        currentClass = enclosingClass;
        return null;
    }

    @Override
     /**
      * Visits and resolves an expression statement.
      * @param stmt The expression statement.
      * Preconditions: stmt != null
      * Postconditions: The expression is resolved.
      */
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
     /**
      * Visits and resolves a function statement, handling its declaration, definition, and body.
      * @param stmt The function statement.
      * Preconditions: stmt != null
      * Postconditions: The function is declared, defined, and its body is resolved in a new scope.
      */
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name);
        define(stmt.name); // Eagerly define the function to allow recursion

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
     /**
      * Visits and resolves an if statement, resolving its condition and branches.
      * @param stmt The if statement.
      * Preconditions: stmt != null
      * Postconditions: The condition and both branches are resolved.
      */
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }

    @Override
     /**
      * Visits and resolves a print statement.
      * @param stmt The print statement.
      * Preconditions: stmt != null
      * Postconditions: The expression to print is resolved.
      */
    public Void visitPrintStmt(Stmt.Print stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
     /**
      * Visits and resolves a return statement, checking for errors and resolving the return value.
      * @param stmt The return statement.
      * Preconditions: stmt != null
      * Postconditions: The return value is resolved and errors are reported for invalid returns.
      */
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Kwii.error(stmt.keyword, "Cannot return from top-level code.");
        }
        if (stmt.value != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                Kwii.error(stmt.keyword, "Cannot return a value from an initializer.");
            }
            resolve(stmt.value);
        }
        return null;
    }

    @Override
     /**
      * Visits and resolves a variable declaration statement.
      * @param stmt The variable statement.
      * Preconditions: stmt != null
      * Postconditions: The variable is declared, its initializer is resolved, and it is defined.
      */
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    @Override
     /**
      * Visits and resolves a while statement, resolving its condition and body.
      * @param stmt The while statement.
      * Preconditions: stmt != null
      * Postconditions: The condition and body are resolved.
      */
    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }

    @Override
     /**
      * Visits and resolves an assignment expression.
      * @param expr The assignment expression.
      * Preconditions: expr != null
      * Postconditions: The value is resolved and the variable is resolved in the current scope.
      */
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
     /**
      * Visits and resolves a binary expression.
      * @param expr The binary expression.
      * Preconditions: expr != null
      * Postconditions: Both operands are resolved.
      */
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
     /**
      * Visits and resolves a function or method call expression.
      * @param expr The call expression.
      * Preconditions: expr != null
      * Postconditions: The callee and all arguments are resolved.
      */
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);

        for (Expr argument : expr.arguments) {
            resolve(argument);
        }
        return null;
    }

    @Override
     /**
      * Visits and resolves a property get expression.
      * @param expr The get expression.
      * Preconditions: expr != null
      * Postconditions: The object whose property is accessed is resolved.
      */
    public Void visitGetExpr(Expr.Get expr) {
        resolve(expr.object);
        return null;
    }

    @Override
     /**
      * Visits and resolves a grouping expression.
      * @param expr The grouping expression.
      * Preconditions: expr != null
      * Postconditions: The inner expression is resolved.
      */
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
     /**
      * Visits and resolves a literal expression (no action needed).
      * @param expr The literal expression.
      * Preconditions: expr != null
      * Postconditions: No resolution is performed.
      */
    public Void visitLiteralExpr(Expr.Literal expr) {
        // No resolution needed for literals
        return null;
    }

    @Override
     /**
      * Visits and resolves a logical (and/or) expression.
      * @param expr The logical expression.
      * Preconditions: expr != null
      * Postconditions: Both operands are resolved.
      */
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
     /**
      * Visits and resolves a property set expression.
      * @param expr The set expression.
      * Preconditions: expr != null
      * Postconditions: The value and object are resolved.
      */
    public Void visitSetExpr(Expr.Set expr) {
        resolve(expr.value);
        resolve(expr.object);
        return null;
    }

    @Override
     /**
      * Visits and resolves a 'super' expression, checking for valid context and resolving the variable.
      * @param expr The super expression.
      * Preconditions: expr != null
      * Postconditions: The super variable is resolved and errors are reported for invalid usage.
      */
    public Void visitSuperExpr(Expr.Super expr) {
        if (currentClass == ClassType.NONE) {
            Kwii.error(expr.keyword, "Cannot use 'super' outside of a class.");
        } else if (currentClass != ClassType.SUBCLASS) {
            Kwii.error(expr.keyword, "Cannot use 'super' in a class with no superclass.");
        }
        resolveLocal(expr, expr.keyword);
        return null;

    }

    @Override
     /**
      * Visits and resolves a 'this' expression, checking for valid context and resolving the variable.
      * @param expr The this expression.
      * Preconditions: expr != null
      * Postconditions: The this variable is resolved and errors are reported for invalid usage.
      */
    public Void visitThisExpr(Expr.This expr) {
        if (currentClass == ClassType.NONE) {
            Kwii.error(expr.keyword, "Cannot use 'this' outside of a class.");
            return null;
        }

        resolveLocal(expr, expr.keyword);
        return null;
    }

    @Override
     /**
      * Visits and resolves a unary expression.
      * @param expr The unary expression.
      * Preconditions: expr != null
      * Postconditions: The operand is resolved.
      */
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    @Override
     /**
      * Visits and resolves a variable expression, checking for use-before-definition and resolving the variable.
      * @param expr The variable expression.
      * Preconditions: expr != null
      * Postconditions: The variable is resolved and errors are reported for invalid usage.
      */
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
            Kwii.error(expr.name, "Cannot read local variable in its own initializer.");
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    private void resolve(Stmt stmt) {
     /**
      * Resolves a single statement by accepting this visitor.
      * @param stmt The statement to resolve.
      */
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
     /**
      * Resolves a single expression by accepting this visitor.
      * @param expr The expression to resolve.
      */
        expr.accept(this);
    }

    private void resolveFunction(Stmt.Function function, FunctionType type) {
     /**
      * Resolves a function's parameters and body in a new scope, tracking the function type.
      * @param function The function statement.
      * @param type     The type of function (FUNCTION, INITIALIZER, METHOD).
      * Preconditions: function != null, type != null
      * Postconditions: The function's parameters and body are resolved in a new scope.
      */
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        for (Token param : function.params) {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    private void beginScope() {
     /**
      * Begins a new variable scope by pushing a new map onto the scope stack.
      * Postconditions: The scope stack has one additional scope.
      */
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
     /**
      * Ends the current variable scope by popping the top map from the scope stack.
      * Postconditions: The scope stack has one fewer scope.
      */
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }

    private void declare(Token name) { 
     /**
      * Declares a variable in the current scope, reporting an error if already declared.
      * @param name The token representing the variable name.
      * Preconditions: name != null
      * Postconditions: The variable is added to the current scope as 'not yet defined'.
      */
        if (scopes.isEmpty()) return;

        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            Kwii.error(name, "Variable with this name already declared in this scope.");
        }
        scope.put(name.lexeme, false);
    }

    private void define(Token name) {
     /**
      * Defines a variable in the current scope, marking it as available for use.
      * @param name The token representing the variable name.
      * Preconditions: name != null
      * Postconditions: The variable is marked as defined in the current scope.
      */
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }

    private void resolveLocal(Expr expr, Token name) {
     /**
      * Resolves a variable or expression to its scope depth for the interpreter.
      * @param expr The expression to resolve.
      * @param name The token representing the variable name.
      * Preconditions: expr != null, name != null
      * Postconditions: The interpreter is informed of the variable's scope depth.
      */
        // Iterate through scopes in reverse order to find the variable
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }
}




