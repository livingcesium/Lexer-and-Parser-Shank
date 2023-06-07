import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//Arrays not implemented yet

/**
 * This class is used to parse a list of tokens into a series of nodes that can be used to generate code. To be paired with a Lexer.
 */
public class Parser {
    private ArrayDeque<Token> tokens; //Use a queue to keep track of tokens, ArrayDeque is the best implementation I came across
    
    private static final tokenType[] KNOWN_TYPES = {
            tokenType.INTEGER, tokenType.REAL, tokenType.STRING, tokenType.BOOLEAN, tokenType.CHARACTER
    };
    
    Parser(ArrayList<Token> tokenList){
        tokens = new ArrayDeque<Token>(tokenList);
    }
    
    private Token matchAndRemove(tokenType type){
        if(tokens.isEmpty()){
            return null;
        }
        
        if(tokens.peek().getType() == type){
            return tokens.poll();
        } 
        //In all other cases
        return null;
    }
    //This method is used to match and remove a token of any known variable type, helps make it easier to add new types
    private Token matchAndRemoveKnownType(){
        if(tokens.isEmpty())
            return null;

        if(List.of(KNOWN_TYPES).contains(tokens.peek().getType())){
            return tokens.poll();
        } else
            return null;

    }
    
    private void expectEndsOfLine() throws SyntaxErrorException {
        Token token;
        boolean found = false;
        while((token = matchAndRemove(tokenType.ENDOFLINE)) != null){
            if(!found) found = true;
        }
        
        if(!found) throw new SyntaxErrorException("Missing End of Line", token.getLineNum());
        
    }
    
    private Token peek(int elements){
        if(elements < 1){
            throw new IllegalArgumentException("Parser method peek() only takes positive integers greater than 0");
        }
        ArrayDeque<Token> tokensCopy = new ArrayDeque<Token>(tokens);
        Token output = null;
        
        if(tokensCopy.size() < elements) return null; //If you try to peek beyond the queue, return null
            
        for (int i = 0; i < elements; i++) {
            output = tokensCopy.poll();
        }
        
        return output;
    }
    
    public Node parse() throws SyntaxErrorException {
        FunctionNode currentFunc = null;
        
        //call function() in loop, add every function to a program node
//        while((current = expression()) != null){
//            
//            if(head == null) head = current;
//            
//            expectEndsOfLine();
//            System.out.println(current.toString());
//        }
        ProgramNode program = new ProgramNode();
        while((currentFunc = function()) != null){
            
            program.addFunction(currentFunc);
            
            expectEndsOfLine();
            System.out.println(currentFunc.toString());
            
        }
        
        return program;
    }
    
    private Node expression() throws SyntaxErrorException {
        if(tokens.isEmpty()){
            return null;
        }
        
        Node firstTerm = term();
        Node secondTerm;
        
        Token operator;
        
        if((operator = matchAndRemove(tokenType.PLUS)) != null || (operator = matchAndRemove(tokenType.MINUS)) != null){
            secondTerm = term();
            
            MathOpNode output = new MathOpNode(firstTerm, secondTerm, operator);
            
            while((operator = matchAndRemove(tokenType.PLUS)) != null || (operator = matchAndRemove(tokenType.MINUS)) != null){
                output = new MathOpNode(output, term(), operator);
            }
            
            return output;
            
        } else return firstTerm;
            
        
    }
    
    private Node boolCompare() throws SyntaxErrorException {
        if(tokens.isEmpty()){
            return null;
        }
        
        Node firstExpression = expression();
        Node secondExpression = null;
        
        Token operator;
        
        if((operator = matchAndRemove(tokenType.EQUAL)) != null || (operator = matchAndRemove(tokenType.NOTEQUAL)) != null || (operator = matchAndRemove(tokenType.GREATER)) != null || (operator = matchAndRemove(tokenType.GREATEREQUAL)) != null || (operator = matchAndRemove(tokenType.LESS)) != null || (operator = matchAndRemove(tokenType.LESSEQUAL)) != null){
            secondExpression = expression();

            return new BooleanCompareNode(firstExpression, secondExpression, operator);
        }

        return firstExpression;
    }
    
    private Node term() throws SyntaxErrorException {
        if(tokens.isEmpty()){
            return null;
        }
        
        Node firstFactor = factor();
        Node secondFactor;
        Token operator;
        if((operator = matchAndRemove(tokenType.MULTIPLY)) != null ||(operator = matchAndRemove(tokenType.DIVIDE)) != null || (operator = matchAndRemove(tokenType.MODULO)) != null){
            
            secondFactor = factor();
            
            MathOpNode output = new MathOpNode(firstFactor, secondFactor, operator);
            
            while((operator = matchAndRemove(tokenType.MULTIPLY)) != null ||(operator = matchAndRemove(tokenType.DIVIDE)) != null || (operator = matchAndRemove(tokenType.MODULO)) != null){
                output = new MathOpNode(output, factor(), operator);
            }
            
            return output;
            
        } else return firstFactor;
    }
    
    private Node factor() throws SyntaxErrorException {
        if(tokens.isEmpty()){
            return null;
        }
        
        Node output;
        
        int negation = 1;
        if (matchAndRemove(tokenType.MINUS) != null) negation = -1;
        Token token;
        
        if ((token = matchAndRemove(tokenType.NUMBER)) != null) {
            
            if(token.getValue().contains(".")){
                return new RealNode(Float.parseFloat(token.getValue()) * negation);
            } else {
                return new IntegerNode(Integer.parseInt(token.getValue()) * negation);
            }
            
        } else if ((token = matchAndRemove(tokenType.IDENTIFIER)) != null) {
            //TODO: Add check here for array references
            return new VariableReferenceNode(token.getValue());
            
        } else if ((token = matchAndRemove(tokenType.STRINGLITERAL)) != null){
            return new StringNode(token.getValue());
        } else if ((token = matchAndRemove(tokenType.CHARACTERLITERAL)) != null){
            return new CharacterNode(token.getValue().charAt(0));
        } else if ((token = matchAndRemove(tokenType.BOOLEANLITERAL)) != null){
            if(token.getValue().equals("true")) 
                return new BooleanNode(true);
            else 
                return new BooleanNode(false);
        } else if (matchAndRemove(tokenType.LEFTPAREN) != null) {
            
            output = boolCompare(); //this defaults to expression() if there is no boolean operator
            if (matchAndRemove(tokenType.RIGHTPAREN) == null) {
                throw new SyntaxErrorException("Missing right parenthesis", tokens.peek().getLineNum());
            }
            
            return output;
        }
        
        throw new SyntaxErrorException("Expected a variable reference, a number, or a parenthesized expression", tokens.peek().getLineNum());
    }
    
    //Function parsing stuff
    
    private ProgramNode program() throws SyntaxErrorException {
        if (tokens.isEmpty()) {
            return null;
        }
        
        ProgramNode output = new ProgramNode();
        FunctionNode current;
        
        while((current = function()) != null){
            output.addFunction(current);
        }
        
        return output;
    }
    
    
    private FunctionNode function() throws SyntaxErrorException {
        
        FunctionNode output;
        
        if (tokens.isEmpty()) {
            return null;
        }
        
        if(matchAndRemove(tokenType.DEFINE) == null){
//            throw new SyntaxErrorException("Expected a function definition", tokens.peek().getLineNum());
            return null;
        }
        
        Token nameToken;
        if((nameToken = matchAndRemove(tokenType.IDENTIFIER)) == null){
            throw new SyntaxErrorException("Expected a function name", tokens.peek().getLineNum());
        }
        
        if(matchAndRemove(tokenType.LEFTPAREN) == null){
            throw new SyntaxErrorException("Expected a left parenthesis", tokens.peek().getLineNum());
        }
        
        ArrayList<VariableNode> parameters = parameterDeclaration();
        
        if(matchAndRemove(tokenType.RIGHTPAREN) == null){
            throw new SyntaxErrorException("Expected a right parenthesis", tokens.peek().getLineNum());
        }
        
        if(matchAndRemove(tokenType.ENDOFLINE) == null){
            throw new SyntaxErrorException("Expected an end of line", tokens.peek().getLineNum());
        }

        ArrayList<VariableNode> variables = new ArrayList<VariableNode>();
        ArrayList<VariableNode> constants = new ArrayList<VariableNode>();
        
        Token temp;
        
        //Grab the variables and constants, should work in any order and with any number of them
        while((temp = matchAndRemove(tokenType.VARIABLES)) != null || (temp = matchAndRemove(tokenType.CONSTANTS)) != null){
            
            if(temp.getType() == tokenType.VARIABLES){
                variables.addAll(variableDeclaration(false));
                matchAndRemove(tokenType.ENDOFLINE);
            } else {
                constants.addAll(variableDeclaration(true));
                matchAndRemove(tokenType.ENDOFLINE);
            }
            
        }
        
        ArrayList<StatementNode> statements = statements();
        
        output = new FunctionNode(nameToken.getValue(), parameters, constants, variables, statements);

        return output;
    }
    
    private ArrayList<VariableNode> parameterDeclaration() throws SyntaxErrorException {
        if (tokens.isEmpty()) {
            return null;
        }
        
        boolean changeable = false;
        boolean parameters = false;
        
        
        
        
        if(matchAndRemove(tokenType.VAR) != null) 
            changeable = true;
        
        Token name;
        Token type;
        
        //TODO: verify if .getValue() is correct in this case
        //TODO: and fix the parameter type Object nonsense
        
        ArrayList<VariableNode> output = new ArrayList<VariableNode>();
        
        do{
            if((name = matchAndRemove(tokenType.IDENTIFIER)) == null){
                return new ArrayList<VariableNode>(); //No parameters, empty list
            }

            if(matchAndRemove(tokenType.DECLARATION) == null){
                throw new SyntaxErrorException("Expected declaration symbol :", tokens.peek().getLineNum());
            }

            if((type = matchAndRemove(tokenType.IDENTIFIER)) == null){
                throw new SyntaxErrorException("Expected a parameter type", tokens.peek().getLineNum());
            }

            VariableNode<Object> variable =  new VariableNode<Object>(name.getValue(), type.getValue(), changeable);
            
            output.add(variable);
            
        } while(matchAndRemove(tokenType.COMMA) != null);
        
        return output;
    }
    
    private ArrayList<VariableNode> variableDeclaration(boolean constant) throws SyntaxErrorException {
        if (tokens.isEmpty()) {
            return null;
        }

        ArrayList<VariableNode> output = new ArrayList<VariableNode>();
        
        String name;

        Token nameToken;
        
        if(constant){
            do{
                if((nameToken = matchAndRemove(tokenType.IDENTIFIER)) == null){
                    throw new SyntaxErrorException("Expected a variable name", tokens.peek().getLineNum());
                }

                //TODO: verify if .getValue() is correct in this case
                name = nameToken.getValue();
                
                if(matchAndRemove(tokenType.EQUAL) == null){
                    throw new SyntaxErrorException("Expected assignment symbol =", tokens.peek().getLineNum());
                }
                Token valueToken;

                int negation = 1;
                if (matchAndRemove(tokenType.MINUS) != null) negation = -1;

                if((valueToken = matchAndRemove(tokenType.NUMBER)) != null){

                    if(valueToken.getValue().contains(".")){
                        output.add(new VariableNode<Float>(Float.parseFloat(valueToken.getValue()) * negation, name,  "real", false));
                    } else {
                        output.add(new VariableNode<Integer>(Integer.parseInt(valueToken.getValue()) * negation, name,  "integer", false));
                    }
                } else if ((valueToken = matchAndRemove(tokenType.STRINGLITERAL)) != null){
                    output.add(new VariableNode<String>(valueToken.getValue(), name,  "string", false));
                } else if ((valueToken = matchAndRemove(tokenType.BOOLEANLITERAL)) != null) {
                    if(valueToken.getValue() == "true"){
                        output.add(new VariableNode<Boolean>(true, name,  "boolean", false));
                    } else {
                        output.add(new VariableNode<Boolean>(false, name,  "boolean", false));
                    }
                } else if ((valueToken = matchAndRemove(tokenType.CHARACTERLITERAL)) != null) {
                    output.add(new VariableNode<Character>(valueToken.getValue().toCharArray()[0], name,  "character", false));
                }
            } while (matchAndRemove(tokenType.COMMA) != null);
            
            
        } else { //Variable declaration
            ArrayList<String> names = new ArrayList<String>();
            
            do{
                //Collect variable names
                
                if((nameToken = matchAndRemove(tokenType.IDENTIFIER)) == null){
                    throw new SyntaxErrorException("Expected a variable name", tokens.peek().getLineNum());
                }

                names.add(nameToken.getValue());
                
            } while (matchAndRemove(tokenType.COMMA) != null);
            
            if(matchAndRemove(tokenType.DECLARATION) == null){
                throw new SyntaxErrorException("Expected declaration symbol :", tokens.peek().getLineNum());
            }
            
            Token typeToken;
            
            if((typeToken = matchAndRemoveKnownType()) == null){
                throw new SyntaxErrorException("Expected a variable type", tokens.peek().getLineNum());
            } else {
                for(String currentName : names){
                    output.add(new VariableNode(currentName, typeToken.getValue(), true));
                }
            }
            
            
        }

        return output;
        
    }    
    
    private StatementNode statement() throws SyntaxErrorException {
        return assignment();
    }
    private ArrayList<StatementNode> statements() throws SyntaxErrorException {
        if (tokens.isEmpty()) {
            return null;
        }
        
        if(matchAndRemove(tokenType.INDENT) == null){
            throw new SyntaxErrorException("Expected indent, function body required", tokens.peek().getLineNum());
        }
            
        ArrayList<StatementNode> output = new ArrayList<StatementNode>();
        
        StatementNode currentStatement;
        while((currentStatement = statement()) != null){
            output.add(currentStatement);
            if(matchAndRemove(tokenType.ENDOFLINE) == null){
                throw new SyntaxErrorException("Expected end of line", tokens.peek().getLineNum());
            }
        }
        
        if(output.isEmpty()) return new ArrayList<StatementNode>(); //No statements in function body
        
        return output;
    }
    private AssignmentNode assignment() throws SyntaxErrorException {
        if (tokens.isEmpty()) {
            return null;
        }
        
        Token name;
        
        if((name = matchAndRemove(tokenType.IDENTIFIER)) == null){
            return null;
        }
        //                                                 v need extra logic to parse this 
        //TODO: Come back and add support for arrays (ex a[0] := 5)
        
        if(matchAndRemove(tokenType.ASSIGN) == null){
            throw new SyntaxErrorException("Expected assignment symbol :=", tokens.peek().getLineNum());
        }
        
        VariableReferenceNode variableReference = new VariableReferenceNode(name.getValue());
        Node value = boolCompare();
        
        if(value == null){
            throw new SyntaxErrorException("Right side of assignment is not a valid expression", tokens.peek().getLineNum());
        }
        
        return new AssignmentNode(variableReference, value);
    }
    

        
    
}




