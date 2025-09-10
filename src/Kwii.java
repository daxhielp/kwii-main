import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Kwii {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false; 
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException{
        if (args.length >  1) {
            System.out.println("Usage: kwii [script]");
            System.exit(64);
        } else if (args.length == 1) {
            if (!args[0].endsWith(".kwii")) {
                System.out.println("Can only interpret .kwii files.");
                System.exit(64);
            }
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException{
        /*
         * Runs kwii with from a source file destination
         */
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        /*
         * Runs kwii from a new terminal window, allowing for line-by-line execution
         */
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

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

        for (;;) {
            System.out.print(">>> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) {
        /*
         * Central run command for kwii
         * Passes the source code to the scanner, which returns a list of tokens
         * The parser then converts the tokens into a list of statements
         */
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        if (hadError) return;

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        if (hadError) return;

        interpreter.interpret(statements);
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}