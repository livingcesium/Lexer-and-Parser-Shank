
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Shank {
    public static void main(String[] args) throws IOException, SyntaxErrorException {
        List<String> lines = null;

        try{
            Path myPath = Paths.get(args[0]);
            lines = Files.readAllLines(myPath, StandardCharsets.UTF_8);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please provide a file name.");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("File not found.");
            System.exit(1);
        }

        ArrayList<String> linesArr = new ArrayList<String>(lines);

        Lexer lexer = new Lexer();
        for (String line : linesArr) {
            lexer.lex(line);
        }
        lexer.concludeLexing();
        ArrayList<Token> tokens = new ArrayList<Token>(lexer.getTokens());

        for (Token token : tokens) {
            System.out.println(token);
        }
        
        System.out.println("\n\n_____________PARSING..._____________\n");
        
        Parser parser = new Parser(tokens);

        Node root = parser.parse();
        
        




    }
}