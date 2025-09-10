// Stmt.java
//
// This file defines the abstract syntax tree (AST) node classes for statements in the language.
// It uses the visitor pattern to allow operations on different statement types.
//
// Usage:
//   Stmt stmt = new Stmt.Expression(...);
//   stmt.accept(visitor);
//
// Preconditions:
//   - AST nodes must be constructed with valid tokens and sub-statements.
// Postconditions:
//   - Statements can be traversed and operated on using the visitor interface.
//
import java.util.List;

/**
 * Abstract base class for all statement AST nodes.
 * Each subclass represents a specific kind of statement.
 */
abstract class Stmt {
    /**
     * Visitor interface for traversing or operating on statements.
     * @param <R> The return type of the visitor's methods.
     */
    interface Visitor<R> {
        R visitBlockStmt(Block stmt);       // Block of statements
        R visitClassStmt(Class stmt);       // Class declaration
        R visitExpressionStmt(Expression stmt); // Expression statement
        R visitFunctionStmt(Function stmt); // Function declaration
        R visitIfStmt(If stmt);             // If statement
        R visitPrintStmt(Print stmt);       // Print statement
        R visitReturnStmt(Return stmt);     // Return statement
        R visitVarStmt(Var stmt);           // Variable declaration
        R visitWhileStmt(While stmt);       // While loop
    }

    /**
     * Block statement: a sequence of statements in a new scope.
     */
    static class Block extends Stmt {
        /**
         * @param statements The list of statements in the block.
         */
        Block(List<Stmt> statements) {
            this.statements = statements;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitBlockStmt method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        final List<Stmt> statements; // The statements in the block
    }

    /**
     * Class declaration statement.
     */
    static class Class extends Stmt {
        /**
         * @param name The class name token.
         * @param superclass The superclass variable (may be null).
         * @param methods The list of method declarations.
         */
        Class(Token name, Expr.Variable superclass, List<Stmt.Function> methods) {
            this.name = name;
            this.superclass = superclass;
            this.methods = methods;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitClassStmt method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitClassStmt(this);
        }

        final Token name; // The class name
        final Expr.Variable superclass; // The superclass (may be null)
        final List<Stmt.Function> methods; // The class methods
    }

    /**
     * Expression statement: evaluates an expression for its side effects.
     */
    static class Expression extends Stmt {
        /**
         * @param expression The expression to evaluate.
         */
        Expression(Expr expression) {
            this.expression = expression;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitExpressionStmt method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        final Expr expression; // The expression to evaluate
    }

    /**
     * Function declaration statement.
     */
    static class Function extends Stmt {
        /**
         * @param name The function name token.
         * @param params The list of parameter tokens.
         * @param body The list of statements in the function body.
         */
        Function(Token name, List<Token> params, List<Stmt> body) {
            this.name = name;
            this.params = params;
            this.body = body;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitFunctionStmt method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }

        final Token name; // The function name
        final List<Token> params; // The function parameters
        final List<Stmt> body; // The function body statements
    }

    /**
     * If statement: conditional execution.
     */
    static class If extends Stmt {
        /**
         * @param condition The condition expression.
         * @param thenBranch The statement to execute if condition is true.
         * @param elseBranch The statement to execute if condition is false (may be null).
         */
        If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitIfStmt method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        final Expr condition; // The condition expression
        final Stmt thenBranch; // The 'then' branch
        final Stmt elseBranch; // The 'else' branch (may be null)
    }

    /**
     * Print statement: prints the result of an expression.
     */
    static class Print extends Stmt {
        /**
         * @param expression The expression to print.
         */
        Print(Expr expression) {
            this.expression = expression;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitPrintStmt method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        final Expr expression; // The expression to print
    }

    /**
     * Return statement: returns a value from a function.
     */
    static class Return extends Stmt {
        /**
         * @param keyword The 'return' keyword token.
         * @param value The expression to return (may be null).
         */
        Return(Token keyword, Expr value) {
            this.keyword = keyword;
            this.value = value;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitReturnStmt method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }

        final Token keyword; // The 'return' keyword
        final Expr value;    // The value to return (may be null)
    }

    /**
     * Variable declaration statement.
     */
    static class Var extends Stmt {
        /**
         * @param name The variable name token.
         * @param initializer The initializer expression (may be null).
         */
        Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitVarStmt method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }

        final Token name;        // The variable name
        final Expr initializer;  // The initializer expression (may be null)
    }

    /**
     * While loop statement.
     */
    static class While extends Stmt {
        /**
         * @param condition The loop condition expression.
         * @param body The body statement to execute while condition is true.
         */
        While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitWhileStmt method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }

        final Expr condition; // The loop condition
        final Stmt body;      // The loop body
    }

    /**
     * Accepts a visitor for this node (abstract method for all subclasses).
     * @param visitor The visitor instance.
     * @param <R> The return type of the visitor.
     * @return The result of the visitor's visit method for this node type.
     */
    abstract <R> R accept(Visitor<R> visitor);
}
