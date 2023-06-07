import java.util.HashMap;

public class ProgramNode<E> extends Node<E>{
    
    private E value;
    private HashMap<String, FunctionNode> functions;
    
    ProgramNode() {
        functions = new HashMap<String, FunctionNode>();
    }
    public void addFunction(FunctionNode function) {
        functions.put(function.getName(), function);
    }
    
    public String ToString() {
        return String.format("programNode(%s)", value);
    }

    public E getValue() {
        return value;
    }
}
