// AstPrinter.java
//
// This class implements a visitor pattern to print the abstract syntax tree (AST) of expressions in a human-readable format.
// It traverses the AST nodes and produces a string representation for debugging and visualization purposes.
//
// Usage:
//   AstPrinter printer = new AstPrinter();
//   String astString = printer.print(expression);
//
// Preconditions:
//   - The input expression must be a valid AST node (Expr).
// Postconditions:
//   - Returns a string representing the structure of the AST.
//
public class AstPrinter implements Expr.Visitor<String> {
	/**
	 * Visits an assignment expression node and returns its string representation.
	 * @param expr The assignment expression node (variable name, value).
	 * @return String representation in parenthesized form.
	 */
	@Override
	public String visitAssignExpr(Expr.Assign expr) {
		// Format: (assign name value)
		return parenthesize("assign " + expr.name.lexeme, expr.value);
	}

	/**
	 * Visits a call expression node and returns its string representation.
	 * @param expr The call expression node (callee, arguments).
	 * @return String representation in parenthesized form.
	 */
	@Override
	public String visitCallExpr(Expr.Call expr) {
		// Format: (call callee arg1 arg2 ...)
		StringBuilder builder = new StringBuilder();
		builder.append(parenthesize("call", expr.callee));
		for (Expr arg : expr.arguments) {
			builder.append(" ").append(arg.accept(this));
		}
		return builder.toString();
	}

	/**
	 * Visits a get expression node (object property access).
	 * @param expr The get expression node (object, property name).
	 * @return String representation in parenthesized form.
	 */
	@Override
	public String visitGetExpr(Expr.Get expr) {
		// Format: (get object property)
		return parenthesize("get " + expr.name.lexeme, expr.object);
	}

	/**
	 * Visits a logical expression node (and/or).
	 * @param expr The logical expression node (left, operator, right).
	 * @return String representation in parenthesized form.
	 */
	@Override
	public String visitLogicalExpr(Expr.Logical expr) {
		// Format: (logical-operator left right)
		return parenthesize(expr.operator.lexeme, expr.left, expr.right);
	}

	/**
	 * Visits a set expression node (object property assignment).
	 * @param expr The set expression node (object, property name, value).
	 * @return String representation in parenthesized form.
	 */
	@Override
	public String visitSetExpr(Expr.Set expr) {
		// Format: (set object property value)
		return parenthesize("set " + expr.name.lexeme, expr.object, expr.value);
	}

	/**
	 * Visits a super expression node (superclass method access).
	 * @param expr The super expression node (keyword, method name).
	 * @return String representation in parenthesized form.
	 */
	@Override
	public String visitSuperExpr(Expr.Super expr) {
		// Format: (super method)
		return parenthesize("super " + expr.method.lexeme);
	}

	/**
	 * Visits a this expression node (current instance reference).
	 * @param expr The this expression node (keyword).
	 * @return String representation in parenthesized form.
	 */
	@Override
	public String visitThisExpr(Expr.This expr) {
		// Format: (this)
		return "this";
	}

	/**
	 * Visits a variable expression node (variable reference).
	 * @param expr The variable expression node (name).
	 * @return String representation in parenthesized form.
	 */
	@Override
	public String visitVariableExpr(Expr.Variable expr) {
		// Format: (var name)
		return parenthesize("var " + expr.name.lexeme);
	}
	/**
	 * Prints the AST for the given expression by visiting its nodes.
	 * @param expr The root expression node to print.
	 * @return A string representation of the AST.
	 * Preconditions: expr must not be null.
	 * Postconditions: Returns a valid string representation of the AST.
	 */
	String print(Expr expr) {
		// Start the visitor traversal from the root node.
		return expr.accept(this);
	}

	/**
	 * Visits a binary expression node and returns its string representation.
	 * @param expr The binary expression node (left, operator, right).
	 * @return String representation in parenthesized form.
	 * Preconditions: expr.left and expr.right must not be null.
	 */
	@Override
	public String visitBinaryExpr(Expr.Binary expr) {
		// Format: (operator left right)
		return parenthesize(expr.operator.lexeme, expr.left, expr.right);
	}

	/**
	 * Visits a grouping expression node and returns its string representation.
	 * @param expr The grouping expression node (expression inside parentheses).
	 * @return String representation in parenthesized form.
	 * Preconditions: expr.expression must not be null.
	 */
	@Override
	public String visitGroupingExpr(Expr.Grouping expr) {
		// Format: (group expression)
		return parenthesize("group", expr.expression);
	}

	/**
	 * Visits a unary expression node and returns its string representation.
	 * @param expr The unary expression node (operator, right operand).
	 * @return String representation in parenthesized form.
	 * Preconditions: expr.right must not be null.
	 */
	@Override
	public String visitUnaryExpr(Expr.Unary expr) {
		// Format: (operator right)
		return parenthesize(expr.operator.lexeme, expr.right);
	}

	/**
	 * Visits a literal expression node and returns its string representation.
	 * @param expr The literal expression node (value).
	 * @return String representation of the literal value, or "nil" if null.
	 */
	@Override
	public String visitLiteralExpr(Expr.Literal expr) {
		// Return "nil" for null values, otherwise the value's string.
		if (expr.value == null) return "nil";
		return expr.value.toString();
	}

	/**
	 * Helper method to format expressions in parenthesized prefix notation.
	 * @param name The name or operator to display.
	 * @param exprs The expressions to include as children.
	 * @return String in the form (name expr1 expr2 ...)
	 * Preconditions: name must not be null; exprs may be empty.
	 */
	private String parenthesize(String name, Expr... exprs) {
		StringBuilder builder = new StringBuilder(); 

		builder.append("(").append(name);
		// Recursively visit each child expression.
		for (Expr expr : exprs) {
			builder.append(" ");
			builder.append(expr.accept(this));
		}
		builder.append(")");

		return builder.toString();
	}
}
