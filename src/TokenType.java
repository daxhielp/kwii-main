/**
 * TokenType.java
 *
 * Enum defining all possible token types for the language.
 * Used by the scanner and parser to classify lexical elements.
 *
 * Usage:
 *   TokenType type = TokenType.IDENTIFIER;
 *
 * Preconditions:
 *   - Enum values must match the language's grammar.
 * Postconditions:
 *   - TokenType values are used throughout the scanner, parser, and interpreter.
 */
enum TokenType {
  // Single-character tokens:
  LEFT_PAREN,    // (
  RIGHT_PAREN,   // )
  LEFT_BRACE,    // {
  RIGHT_BRACE,   // }
  COMMA,         // ,
  DOT,           // .
  MINUS,         // -
  PLUS,          // +
  SEMICOLON,     // ;
  SLASH,         // /
  STAR,          // *

  // One or two character tokens:
  BANG,          // !
  BANG_EQUAL,    // !=
  EQUAL,         // =
  EQUAL_EQUAL,   // ==
  GREATER,       // >
  GREATER_EQUAL, // >=
  LESS,          // <
  LESS_EQUAL,    // <=
  PLUS_PLUS,     // ++
  PLUS_EQUAL,    // +=
  MINUS_MINUS,   // --
  MINUS_EQUAL,   // -=
  STAR_EQUALS,   // *=
  SLASH_EQUALS,  // /=

  // Literals:
  IDENTIFIER,    // variable/function/class names
  STRING,        // string literals
  NUMBER,        // numeric literals

  // Keywords:
  AND,           // 'and' logical operator
  CLASS,         // 'class' keyword
  ELSE,          // 'else' keyword
  FALSE,         // 'false' boolean literal
  FUN,           // 'fun' (function) keyword
  FOR,           // 'for' loop keyword
  IF,            // 'if' conditional keyword
  NIL,           // 'nil' (null) literal
  OR,            // 'or' logical operator
  PRINT,         // 'print' statement
  RETURN,        // 'return' statement
  SUPER,         // 'super' keyword for superclass access
  THIS,          // 'this' keyword for current instance
  TRUE,          // 'true' boolean literal
  VAR,           // 'var' variable declaration
  WHILE,         // 'while' loop keyword

  EOF            // End of file/input
}