
/**
 * Token.java
 *
 * Represents a lexical token produced by the scanner.
 * Stores the token type, lexeme, literal value, and source line number.
 */
class Token {
    /**
     * The type of token (e.g., IDENTIFIER, NUMBER, PLUS, etc.).
     */
    final TokenType type;

    /**
     * The actual string of characters from the source code that this token represents.
     */
    final String lexeme;

    /**
     * The literal value of the token, if any (e.g., number value, string contents). May be null.
     */
    final Object literal;

    /**
     * The line number in the source code where this token appears.
     */
    final int line;

    /**
     * Constructs a new Token instance.
     * @param type The type of token.
     * @param lexeme The string representation from the source code.
     * @param literal The literal value (may be null).
     * @param line The line number in the source code.
     * Preconditions: type and lexeme must not be null; line >= 1.
     * Postconditions: All fields are initialized.
     */
    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    /**
     * Returns a string representation of the token for debugging and error reporting.
     * @return A string in the format: type lexeme literal
     */
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
