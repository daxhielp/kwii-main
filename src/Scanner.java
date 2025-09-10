// Scanner.java
//
// This class implements a lexical scanner (tokenizer) for the language.
// It converts source code strings into a list of tokens for parsing.
//
// Usage:
//   Scanner scanner = new Scanner(source);
//   List<Token> tokens = scanner.scanTokens();
//
// Preconditions:
//   - The source code string must be non-null and valid.
// Postconditions:
//   - Returns a list of tokens representing the lexical structure of the source code.

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Scanner.java
 *
 * Implements a lexical scanner (tokenizer) for the language.
 * Converts source code strings into a list of tokens for parsing.
 */
class Scanner {
    /**
     * The source code string to scan.
     */
    private final String source;

    /**
     * The list of tokens produced by scanning the source.
     */
    private final List<Token> tokens = new ArrayList<>();

    /**
     * Map of reserved keywords to their corresponding token types.
     */
    private static final Map<String, TokenType> keywords;

    /**
     * The start index of the current lexeme being scanned.
     */
    private int start = 0;
    /**
     * The current index in the source string.
     */
    private int current = 0;
    /**
     * The current line number in the source code.
     */
    private int line = 1;

    // Static initializer for the keywords map.
    static {
        keywords = new HashMap<>();
        keywords.put("and",    TokenType.AND);
        keywords.put("class",  TokenType.CLASS);
        keywords.put("else",   TokenType.ELSE);
        keywords.put("false",  TokenType.FALSE);
        keywords.put("for",    TokenType.FOR);
        keywords.put("fun",    TokenType.FUN);
        keywords.put("if",     TokenType.IF);
        keywords.put("nil",    TokenType.NIL);
        keywords.put("or",     TokenType.OR);
        keywords.put("print",  TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super",  TokenType.SUPER);
        keywords.put("this",   TokenType.THIS);
        keywords.put("true",   TokenType.TRUE);
        keywords.put("var",    TokenType.VAR);
        keywords.put("while",  TokenType.WHILE);
    }

    /**
     * Constructs a new Scanner for the given source code.
     * @param source The source code string to scan.
     * Preconditions: source must not be null.
     * Postconditions: Scanner is initialized and ready to scan.
     */
    Scanner(String source) {
        this.source = source;
    }

    /**
     * Scans the entire source code and returns a list of tokens.
     * @return List of tokens representing the lexical structure of the source code.
     * Postconditions: The returned list includes an EOF token at the end.
     */
    List<Token> scanTokens() {
        // Iterate through the input, scanning one token at a time.
        while (!isAtEnd()){
            start = current;
            scanToken();
        }
        // Add an EOF token at the end of the input.
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    /**
     * Scans a single token from the source and adds it to the tokens list.
     * Handles all token types, including comments and whitespace.
     */
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;
            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '/':
                // Handle single-line and block comments.
                if (match('/')) {
                    // Single-line comment: consume until end of line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    // Block comment: consume until closing */
                    skipBlockComment();
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                // Newline: increment line counter.
                line++;
                break;
            case '"':
                // String literal.
                string();
                break;
            default:
                if (isDigit(c)) {
                    // Number literal.
                    number();
                } else if (isAlpha(c)) {
                    // Identifier or keyword.
                    identifier();
                } else {
                    // Unexpected character: report error.
                    Kwii.error(line, "Unexpected character: " + c);
                }
                break;
        }
    }

    /**
     * Skips over a block comment (/* ... * /) in the source code.
     * Reports an error if the comment is unterminated.
     */
    private void skipBlockComment() {
        while (!isAtEnd()) {
            if (peek() == '*' && peekNext() == '/') {
                advance();
                advance();
                return;
            } else if (peek() == '\n') {
                line++;
            }
            advance();
        }
        Kwii.error(line, "Unterminated block comment.");
    }

    /**
     * Scans an identifier or keyword from the source.
     * Adds the appropriate token to the tokens list.
     */
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;
        addToken(type);
    }

    /**
     * Checks if a character is a digit (0-9).
     * @param c The character to check.
     * @return True if c is a digit, false otherwise.
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Scans a number literal from the source and adds it as a token.
     * Handles floating-point numbers.
     */
    private void number() {
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
        }
        while (isDigit(peek())) advance();
        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    /**
     * Scans a string literal from the source and adds it as a token.
     * Handles multi-line strings and reports unterminated strings.
     */
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        if (isAtEnd()) {
            Kwii.error(line, "Unterminated string.");
            return;
        }
        advance(); // Consume closing quote
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    /**
     * Checks if the next character matches the expected character.
     * Advances the current index if matched.
     * @param exp The expected character.
     * @return True if matched, false otherwise.
     */
    private boolean match(char exp) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != exp) return false;
        current++;
        return true;
    }

    /**
     * Peeks at the current character without consuming it.
     * @return The current character, or '\0' if at end of source.
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    /**
     * Peeks at the next character without consuming it.
     * @return The next character, or '\0' if at end of source.
     */
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    /**
     * Checks if a character is an alphabetic character or underscore.
     * @param c The character to check.
     * @return True if c is a letter or underscore, false otherwise.
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c == '_');
    }

    /**
     * Checks if a character is alphanumeric (letter, digit, or underscore).
     * @param c The character to check.
     * @return True if c is alphanumeric, false otherwise.
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * Checks if the scanner has reached the end of the source.
     * @return True if at end, false otherwise.
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Consumes and returns the current character, advancing the index.
     * @return The consumed character.
     */
    private char advance() {
        return source.charAt(current++);
    }

    /**
     * Adds a token of the given type with no literal value.
     * @param type The token type.
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Adds a token of the given type and literal value.
     * @param type The token type.
     * @param literal The literal value (may be null).
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
