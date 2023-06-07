import java.util.*;

enum State {
    START, WORD, NUMBER, DECIMAL, PUNCTUATION, STRING, COMMENT
}


 /**
  * This class is the lexer for the Shank programming language. It takes in a string of code and converts it into a list of tokens.
  */
public class Lexer {
    //fields

    HashMap<String, tokenType> knownWords = new HashMap<String, tokenType>(){
        {
            put("define", tokenType.DEFINE);
            put("var", tokenType.VAR);
            put("if", tokenType.IF);
            put("then", tokenType.THEN);
            put("else", tokenType.ELSE);
            put("elsif", tokenType.ELSIF);
            put("while", tokenType.WHILE);
            put("repeat", tokenType.REPEAT);
            put("until", tokenType.UNTIL);
            put("for", tokenType.FOR);
            put("from", tokenType.FROM);
            put("to", tokenType.TO);
            put("variables", tokenType.VARIABLES);
            put("constants", tokenType.CONSTANTS);
            put("write", tokenType.WRITE);
            put("integer", tokenType.INTEGER);
            put("real", tokenType.REAL);
            put("string", tokenType.STRING);
            put("boolean", tokenType.BOOLEAN);
            put("true", tokenType.BOOLEANLITERAL);
            put("false", tokenType.BOOLEANLITERAL);
        }
    };

    HashMap<String, tokenType> knownPunctuation = new HashMap<String, tokenType>(){
        {
            put("+", tokenType.PLUS);
            put("-", tokenType.MINUS);
            put("–", tokenType.MINUS); //unicode for minus sign, stupid edge case
            put("*", tokenType.MULTIPLY);
            put("/", tokenType.DIVIDE);
            put("%", tokenType.MODULO);
            put("=", tokenType.EQUAL);
            put(":=", tokenType.ASSIGN);
            put("<", tokenType.LESS);
            put(">", tokenType.GREATER);
            put("<=", tokenType.LESSEQUAL);
            put(">=", tokenType.GREATEREQUAL);
            put("!=", tokenType.NOTEQUAL);
            put("(", tokenType.LEFTPAREN);
            put(")", tokenType.RIGHTPAREN);
            put("{", tokenType.LEFTBRACE);
            put("}", tokenType.RIGHTBRACE);
            put(":", tokenType.DECLARATION);
            put(",", tokenType.COMMA);
            put("\"", tokenType.QUOTE);
        }
    };

    private ArrayList<Token> tokens;
    private State state;
    private String accumulator = "";
    private int line = 0;
    private int lastIndentLevel = 0;
    private int indentLevel = 0;
    private boolean comment = false;

    //constructors
    public Lexer() {
        state = State.START;
        tokens = new ArrayList<Token>();
    }
    //methods
    public void lex(String input) throws SyntaxErrorException {
        line++;
        System.out.println("Lexing: " + input + " on line " + line);
        Token token = null;
        boolean dentFound = false;
        int spaceCount = 0;
        indentLevel = 0;
        for(char c : input.toCharArray()){
            switch (state) {
                case START -> {
                    if (comment){
                        state = State.COMMENT;
                    } else if (Character.isDigit(c)) {
                        accumulator = Character.toString(c);
                        state = State.NUMBER;
                    } else if (Character.isLetter(c)) {
                        accumulator = Character.toString(c);
                        state = State.WORD;
                    } else if (c == ' ' || c == '\t') {
                        
                        tokenType type;
                        if((type = checkKnownPunctuation(accumulator)) != tokenType.UNKNOWN){
                            token = new Token(type, accumulator, line);
                            tokens.add(token);
                            token = null;    
                        }
                        
                        
                        accumulator = Character.toString(c);

                        spaceCount = (c == ' ') ? spaceCount + 1 : spaceCount + 4; //first case is space, second case is tab
                        indentLevel = spaceCount / 4;

                        if(spaceCount % 4 == 0 && indentLevel > lastIndentLevel){ //new indent token required
                            dentFound = true;
                            token = new Token(tokenType.INDENT, "", line);
                            accumulator = "";

                        }
                        //no dent found

                    } else if (c == '.') {
                        accumulator = Character.toString(c);
                        state = State.DECIMAL;
                    }else if (Character.toString(c).equals("\"")) {
                        accumulator = Character.toString(c);
                        state = State.STRING;
                    }else if (knownPunctuation.containsKey(Character.toString(c))) {
                        accumulator = Character.toString(c);
                        state = State.PUNCTUATION;
                    }

                    if (indentLevel < lastIndentLevel) { //dedent case
                        for (int i = indentLevel; i < lastIndentLevel; i++) {
                            token = new Token(tokenType.DEDENT, "", line);
                            tokens.add(token);
                            token = null;
                        }
                    }
                }
                case COMMENT -> {
                    if (c == '}'){
                        accumulator = "";
                        comment = false;
                        state = State.START;
                    } else {
                        //Keep COMMENT state
                    }
                }
                case NUMBER -> {
                    if (Character.isDigit(c)) {
                        accumulator += c;
                        //Keep NUMBER state
                    } else if (c == '.') {
                        accumulator += c;
                        state = State.DECIMAL;
                    } else if (checkKnownPunctuation(Character.toString(c)) != tokenType.UNKNOWN) {
                        token = new Token(tokenType.NUMBER, accumulator, line);
                        accumulator = Character.toString(c);
                        state = State.PUNCTUATION;
                    } else {
                        token = new Token(tokenType.NUMBER, accumulator, line);
                        accumulator = Character.toString(c);
                        state = State.START;
                    }
                }
                case WORD -> {
                    if (Character.isLetter(c)) {
                        accumulator += c;
                        //Keep WORD state
                    } else if (Character.isDigit(c)) {
                        accumulator += c;
                        //Keep WORD state
                    } else {
                        if(accumulator.length() > 1){
                            tokenType type = checkKnownWords(accumulator);
                            token = new Token(type, accumulator, line);

                            accumulator = Character.toString(c);
                        } else {
                            
                        }
                        state = State.START;
                    }
                }
                case DECIMAL -> {
                    if (Character.isDigit(c)) {
                        accumulator += c;
                        //Keep PUNCTUATION state
                    } else {
                        token = new Token(tokenType.NUMBER, accumulator, line);
                        accumulator = Character.toString(c);
                        state = State.START;
                    }
                }
                case PUNCTUATION -> {
                    if (accumulator.equals("{")){
                      comment = true;  
                      state = State.COMMENT;

                    } else if (accumulator.equals(":") || accumulator.equals("<") || accumulator.equals(">") || accumulator.equals("!")) { //these are the only punctuation that can be 2 characters
                        if (c == '=') {
                            //if you have a '=', you have a known punctuation
                            accumulator += c;
                            tokenType type = checkKnownPunctuation(accumulator);
                            //error handle if type is unknown
                            if (type == tokenType.UNKNOWN)
                                throw new SyntaxErrorException("Unknown operator: " + accumulator, line);
                            token = new Token(type, accumulator, line);
                            accumulator = "";
                            state = State.START;
                        } else if (accumulator.equals("!")){ //if you have '!' with no '=', you have garbage
                            throw new SyntaxErrorException("Unknown operator: " + accumulator, line);
                        } else {
                            //now you surely have ':' or '<' or '>' with no '=' so we're done

                            token = new Token(checkKnownPunctuation(accumulator), accumulator, line);
                            accumulator = "";
                            state = State.START;
                        }
                    } else if(accumulator.equals("\"")) {
                        if(c == '\"'){ //Empty string edge case
                            accumulator += c;
                            token = new Token(tokenType.STRINGLITERAL, accumulator, line);
                            accumulator = "";
                            state = State.START;
                        } else { //Normal string
                            accumulator += c;
                            state = State.STRING;
                        }
                    } else if(checkKnownPunctuation(accumulator) == tokenType.LEFTPAREN){ 
                        
                        //Instantly dump the token into the token list, so we can move on immediately
                        token = new Token(tokenType.LEFTPAREN, accumulator, line);
                        tokens.add(token);
                        token = null;

                        //Parentheses can pivot as if they were the start state, so the parentheses logic is similar to the start state logic
                        if (Character.isDigit(c)) {
                            accumulator = Character.toString(c);
                            state = State.NUMBER;
                        } else if (Character.isLetter(c)) {
                            accumulator = Character.toString(c);
                            state = State.WORD;
                        } else if (c == '.') {
                            accumulator = Character.toString(c);
                            state = State.DECIMAL;
                        } else {
                            accumulator = Character.toString(c);
                            state = State.START;
                        }
                        
                        
                    } else {
                        tokenType type = checkKnownPunctuation(accumulator);
                        if (type == tokenType.UNKNOWN)
                            throw new SyntaxErrorException("Unknown operator: " + accumulator, line);
                        token = new Token(type, accumulator, line);
                        accumulator = Character.toString(c);
                        state = State.START;
                    }
                }

                case STRING -> {
                    if (c == '\"') {
                        accumulator += c;
                        token = new Token(tokenType.STRINGLITERAL, accumulator, line);
                        accumulator = "";
                        state = State.START;
                    } else {
                        accumulator += c;
                        //keep string state
                    }
                }
            }


            if (token != null) {
                tokens.add(token);
                token = null;
            }
            
            //Ensure the accumulator is not being ignored if it matches a format we can use. Probably a better way to do this but it works
            if(!accumulator.equals("") && state == State.START){
                if(accumulator.matches("[0-9]+")){
                    state = State.NUMBER;
                } else if(accumulator.matches("[a-zA-Z0-9]+")){
                    state = State.WORD;
                } else if(checkKnownPunctuation(accumulator) != tokenType.UNKNOWN){
                    state = State.PUNCTUATION;
                }
            }
            
            
        }
        
        if(!comment){
            tokenType type = checkKnownPunctuation(accumulator);

            //Catch any remaining punctuation. May be redundant, but it's here just in case. Good coding practices only
            if(type != tokenType.UNKNOWN){
                token = new Token(type, accumulator, line);
                tokens.add(token);
                token = null;
                accumulator = "";
                state = State.START;
                
            }

            //Add remaining accumulated token if any
            switch (state) {
                case NUMBER, DECIMAL -> {
                    token = new Token(tokenType.NUMBER, accumulator, line);
                    accumulator = "";
                    tokens.add(token);
                    state = State.START;
                }
                case WORD -> {
                    type = checkKnownWords(accumulator);
                    token = new Token(type, accumulator, line);
                    accumulator = "";
                    tokens.add(token);
                    state = State.START;
                }
                case PUNCTUATION -> {
                    type = checkKnownPunctuation(accumulator);
                    if (type == tokenType.UNKNOWN)
                        throw new SyntaxErrorException("Unknown operator: " + accumulator, line);
                    token = new Token(type, accumulator, line);
                    accumulator = "";
                    tokens.add(token);
                    state = State.START;
                }
            }

            if (accumulator == " " || accumulator == "\t") { //repeat of indent logic for if the last character is an indent

                spaceCount = (accumulator == " ") ? spaceCount + 1 : spaceCount + 4; //first case is space, second case is tab
                indentLevel = spaceCount / 4;

                if (spaceCount % 4 == 0) { //new indent token required (4 spaces = 1 indent)
                    dentFound = true;
                    if (indentLevel > lastIndentLevel) {
                        token = new Token(tokenType.INDENT, "", line);
                    }

                }
            }

            if(dentFound) {
                lastIndentLevel = indentLevel;
                dentFound = false;
            }

            indentLevel = 0;

            tokens.add(new Token(tokenType.ENDOFLINE, "", line));    
        }


    }

    public ArrayList<Token> getTokens() {
        return new ArrayList<Token>(tokens);
    }
    public State getState() {
        return state;
    }

    public tokenType checkKnownWords(String word) {
        return knownWords.getOrDefault(word, tokenType.IDENTIFIER);
    }

    public tokenType checkKnownPunctuation(String word) {
        return knownPunctuation.getOrDefault(word, tokenType.UNKNOWN);
    }
    
    public int getLine() {
        return line;
    }
    
    public void concludeLexing() {
        Token token;
        if (lastIndentLevel > 0) { //dedent case
            for (int i = 0; i < lastIndentLevel; i++) {
                line++;
                token = new Token(tokenType.DEDENT, "", line);
                tokens.add(token);
            }
            tokens.add(new Token(tokenType.ENDOFLINE, "", line));
            token = null;
        }
    }    

}

//Syntax Error Exception
class SyntaxErrorException extends Exception {
    public SyntaxErrorException(String message, int line) {
        super(message + " at line " + line);
    }
}