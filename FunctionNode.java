import javax.lang.model.type.NullType;
import java.util.ArrayList;

public class FunctionNode<E> extends Node<E>{
    
    private E value;
    private String name;
    private ArrayList<VariableNode> parameters;
    private ArrayList<VariableNode> constants;
    private ArrayList<VariableNode> variables;
    private ArrayList<StatementNode> statements;
    
    FunctionNode(String name, ArrayList<VariableNode> parameters, ArrayList<VariableNode> constants, ArrayList<VariableNode> variables, ArrayList<StatementNode> statements) {
        this.name = name;
        this.parameters = parameters;
        this.constants = constants;
        this.variables = variables;
        this.statements = statements;
    }
    
    
    public String toString() {
        return String.format("functionNode(name: %s, params: {%s}, consts: {%s}, vars: {%s}, statements: {\n\t%s\n})", name, parameters, constants, variables, statements);
        
    }

    public E getValue() {
        return value;
    }
    
    public String getName() {
        return name;
    }
    public ArrayList<VariableNode> getParameters() {
        return parameters;
    }
    
    public ArrayList<VariableNode> getConstants() {
        return constants;
    }
    
    public ArrayList<VariableNode> getVariables() {
        return variables;
    }
    
    public ArrayList<StatementNode>  getStatements() {
        return statements;
    }
    
    //set statements
    public void reassignStatements(ArrayList<StatementNode>  statements) {
        this.statements = statements;
    }
}
