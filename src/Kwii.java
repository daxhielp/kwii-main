// Kwii.java
//
// This is the main entry point for the interpreter. It handles command-line arguments, file reading, and REPL loop.
// It initializes the interpreter and manages error reporting.
//
// Author: Daxhiel Perugorria Ruciel
// Date: 9/9/2025
//
// Usage:
//   java Kwii [script]
//
// Preconditions:
//   - Input files must be valid source code.
// Postconditions:
//   - The interpreter is run on the provided source code or in interactive mode.
//
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Kwii {
    // The main interpreter instance used for executing code.
    private static final Interpreter interpreter = new Interpreter();
    // Flag to indicate if a compile-time error has occurred.
    static boolean hadError = false; 
    // Flag to indicate if a runtime error has occurred.
    static boolean hadRuntimeError = false;

    /**
     * Main entry point for the Kwii interpreter.
     * Handles command-line arguments and determines whether to run a file or start the REPL.
     *
     * @param args Command-line arguments. If present, should be a single .kwii file.
     * @throws IOException if file reading fails or input stream fails.
     *
     * Preconditions:
     *   - args.length <= 1
     *   - If args.length == 1, args[0] must end with ".kwii"
     * Postconditions:
     *   - Interpreter runs the file or starts the REPL.
     */
    public static void main(String[] args) throws IOException{
        // If more than one argument, print usage and exit with error code 64.
        if (args.length >  1) {
            System.out.println("Usage: kwii [script]");
            System.exit(64);
        } else if (args.length == 1) {
            // Only .kwii files are supported.
            if (!args[0].endsWith(".kwii")) {
                System.out.println("Can only interpret .kwii files.");
                System.exit(64);
            }
            // Run the provided file.
            runFile(args[0]);
        } else {
            // No arguments: start the interactive prompt (REPL).
            runPrompt();
        }
    }

    /**
     * Reads and executes a Kwii source file.
     *
     * @param path Path to the .kwii source file.
     * @throws IOException if file reading fails.
     *
     * Preconditions:
     *   - path must be a valid file path to a .kwii file.
     * Postconditions:
     *   - The file is executed. If errors occur, exits with appropriate error code.
     */
    private static void runFile(String path) throws IOException{
        // Read all bytes from the file and convert to a string using the default charset.
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Exit with error code if a compile-time or runtime error occurred.
        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    /**
     * Starts the interactive prompt (REPL) for Kwii.
     * Reads user input line by line and executes it immediately.
     *
     * @throws IOException if input stream fails.
     *
     * Preconditions:
     *   - System.in is available for reading.
     * Postconditions:
     *   - User can enter and execute code interactively until EOF or exit.
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        // Print ASCII art and welcome message.
        System.out.print("                                                   \r\n" + //
                        "                                                   \r\n" + //
                        "                            ..-..                  \r\n" + //
                        "                      .+##############-            \r\n" + //
                        "                    #####################-         \r\n" + //
                        "           +###############################        \r\n" + //
                        "         +##################################       \r\n" + //
                        "         ###################################-      \r\n" + //
                        "        .###################################+      \r\n" + //
                        "        .#######.-##########################+      \r\n" + //
                        "        #-         #########################-      \r\n" + //
                        "       +            -#######################       \r\n" + //
                        "      +                +###################.       \r\n" + //
                        "     -                     -#############+         \r\n" + //
                        "     .                       +#.  #####.           \r\n" + //
                        "                             #+    +#+             \r\n" + //
                        "                             #     .-              \r\n" + //
                        "                         .-#-      #               \r\n" + //
                        "                                .##-               \r\n" + //
                        "                                                   \r\n" + //
                        "                                                   \r\n" + //
                        "                                                       \r\n");

        System.out.println("Welcome to the Kwii REPL!");
        System.out.println("Press Ctrl+C to exit.");
        System.out.println("---------------------------------------------------");

        // Infinite loop for REPL: read, execute, repeat.
        for (;;) {
            System.out.print(">>> "); // Prompt
            String line = reader.readLine(); // Read user input
            if (line == null) break; // Exit on EOF
            run(line); // Execute the input line
            hadError = false; // Reset error flag for next input
        }
    }

    /**
     * Compiles and executes a string of Kwii source code.
     *
     * @param source - The source code to execute.
     *
     * Implementation details:
     *   - Scans the source into tokens.
     *   - Parses tokens into statements.
     *   - Resolves variable scopes.
     *   - Interprets the statements.
     *
     * Preconditions:
     *   - source is a valid string of Kwii code.
     * Postconditions:
     *   - Code is executed, or errors are reported.
     */
    private static void run(String source) {
        // Step 1: Lexical analysis (tokenization)
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        // Step 2: Parsing (syntax analysis)
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // If a syntax error occurred, abort execution.
        if (hadError) return;

        // Step 3: Semantic analysis (variable resolution)
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        // If a resolution error occurred, abort execution.
        if (hadError) return;

        // Step 4: Interpretation (execution)
        interpreter.interpret(statements);
    }

    /**
     * Reports a compile-time error at a specific line.
     *
     * @param line The line number where the error occurred.
     * @param message The error message to display.
     *
     * Postconditions:
     *   - Error is reported to stderr and hadError is set to true.
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * Reports a compile-time error at a specific token.
     *
     * @param token The token where the error occurred.
     * @param message The error message to display.
     *
     * Postconditions:
     *   - Error is reported to stderr and hadError is set to true.
     */
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    /**
     * Reports a runtime error.
     *
     * @param error The RuntimeError object containing error details.
     *
     * Postconditions:
     *   - Error is reported to stderr and hadRuntimeError is set to true.
     */
    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

    /**
     * Helper method to print error messages in a consistent format.
     *
     * @param line The line number where the error occurred.
     * @param where Additional context (e.g., token location).
     * @param message The error message to display.
     *
     * Postconditions:
     *   - Error is reported to stderr and hadError is set to true.
     */
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}