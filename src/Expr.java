// Expr.java
//
// This file defines the abstract syntax tree (AST) node classes for expressions in the language.
// It uses the visitor pattern to allow operations on different expression types.
//
// Usage:
//   Expr expr = new Expr.Binary(...);
//   expr.accept(visitor);
//
// Preconditions:
//   - AST nodes must be constructed with valid tokens and sub-expressions.
// Postconditions:
//   - Expressions can be traversed and operated on using the visitor interface.
//
import java.util.List;

/**
 * Abstract base class for all expression AST nodes.
 * Each subclass represents a specific kind of expression.
 */
abstract class Expr {
    /**
     * Visitor interface for traversing or operating on expressions.
     * @param <R> The return type of the visitor's methods.
     */
    interface Visitor<R> {
        R visitAssignExpr(Assign expr);     // Assignment expression
        R visitBinaryExpr(Binary expr);     // Binary operation expression
        R visitCallExpr(Call expr);         // Function or method call
        R visitGetExpr(Get expr);           // Object property access
        R visitGroupingExpr(Grouping expr); // Parenthesized expression
        R visitLiteralExpr(Literal expr);   // Literal value
        R visitLogicalExpr(Logical expr);   // Logical (and/or) expression
        R visitSetExpr(Set expr);           // Object property assignment
        R visitSuperExpr(Super expr);       // Superclass method access
        R visitThisExpr(This expr);         // This reference
        R visitUnaryExpr(Unary expr);       // Unary operation
        R visitVariableExpr(Variable expr); // Variable reference
    }

    /**
     * Assignment expression: variable assignment (e.g., x = 5)
     */
    static class Assign extends Expr {
        /**
         * @param name The variable being assigned to.
         * @param value The value being assigned.
         */
        Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitAssignExpr method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

        final Token name; // The variable name
        final Expr value; // The value to assign
    }

    /**
     * Binary operation expression (e.g., a + b)
     */
    static class Binary extends Expr {
        /**
         * @param left The left operand.
         * @param operator The operator token.
         * @param right The right operand.
         */
        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitBinaryExpr method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        final Expr left;      // Left operand
        final Token operator; // Operator token
        final Expr right;     // Right operand
    }

    /**
     * Function or method call expression (e.g., foo(1, 2))
     */
    static class Call extends Expr {
        /**
         * @param callee The expression being called (function or class).
         * @param paren The closing parenthesis token.
         * @param arguments The list of argument expressions.
         */
        Call(Expr callee, Token paren, List<Expr> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitCallExpr method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }

        final Expr callee;         // The function or class being called
        final Token paren;         // The closing parenthesis
        final List<Expr> arguments; // The argument expressions
    }

    /**
     * Object property access expression (e.g., obj.prop)
     */
    static class Get extends Expr {
        /**
         * @param object The object whose property is accessed.
         * @param name The property name token.
         */
        Get(Expr object, Token name) {
            this.object = object;
            this.name = name;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitGetExpr method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetExpr(this);
        }

        final Expr object; // The object being accessed
        final Token name;  // The property name
    }

    /**
     * Parenthesized (grouping) expression (e.g., (a + b))
     */
    static class Grouping extends Expr {
        /**
         * @param expression The expression inside the parentheses.
         */
        Grouping(Expr expression) {
            this.expression = expression;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitGroupingExpr method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final Expr expression; // The grouped expression
    }

    /**
     * Literal value expression (e.g., 123, "hello")
     */
    static class Literal extends Expr {
        /**
         * @param value The literal value (may be null).
         */
        Literal(Object value) {
            this.value = value;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitLiteralExpr method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value; // The literal value
    }

    /**
     * Logical (and/or) expression (e.g., a && b)
     */
    static class Logical extends Expr {
        /**
         * @param left The left operand.
         * @param operator The logical operator token.
         * @param right The right operand.
         */
        Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitLogicalExpr method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }

        final Expr left;      // Left operand
        final Token operator; // Logical operator
        final Expr right;     // Right operand
    }

    /**
     * Object property assignment expression (e.g., obj.prop = value)
     */
    static class Set extends Expr {
        /**
         * @param object The object whose property is being set.
         * @param name The property name token.
         * @param value The value to assign.
         */
        Set(Expr object, Token name, Expr value) {
            this.object = object;
            this.name = name;
            this.value = value;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitSetExpr method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSetExpr(this);
        }

        final Expr object; // The object being modified
        final Token name;  // The property name
        final Expr value;  // The value to assign
    }

    /**
     * Superclass method access expression (e.g., super.method())
     */
    static class Super extends Expr {
        /**
         * @param keyword The 'super' keyword token.
         * @param method The method name token.
         */
        Super(Token keyword, Token method) {
            this.keyword = keyword;
            this.method = method;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitSuperExpr method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSuperExpr(this);
        }

        final Token keyword; // The 'super' keyword
        final Token method;  // The method name
    }

    /**
     * This reference expression (e.g., this)
     */
    static class This extends Expr {
        /**
         * @param keyword The 'this' keyword token.
         */
        This(Token keyword) {
            this.keyword = keyword;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitThisExpr method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitThisExpr(this);
        }

        final Token keyword; // The 'this' keyword
    }

    /**
     * Unary operation expression (e.g., -a, !b)
     */
    static class Unary extends Expr {
        /**
         * @param operator The unary operator token.
         * @param right The operand expression.
         */
        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitUnaryExpr method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final Token operator; // The unary operator
        final Expr right;     // The operand
    }

    /**
     * Variable reference expression (e.g., x)
     */
    static class Variable extends Expr {
        /**
         * @param name The variable name token.
         */
        Variable(Token name) {
            this.name = name;
        }

        /**
         * Accepts a visitor for this node.
         * @param visitor The visitor instance.
         * @return The result of the visitor's visitVariableExpr method.
         */
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        final Token name; // The variable name
    }

    /**
     * Accepts a visitor for this node (abstract method for all subclasses).
     * @param visitor The visitor instance.
     * @param <R> The return type of the visitor.
     * @return The result of the visitor's visit method for this node type.
     */
    abstract <R> R accept(Visitor<R> visitor);
}
