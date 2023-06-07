public class VariableReferenceNode extends Node{
    
    String name;
    Node arrayIndexExpression;
    
    VariableReferenceNode(String name) {
        this.name = name;
    }
    VariableReferenceNode(String name, Node arrayIndexExpression) {
        this.name = name;
        this.arrayIndexExpression = arrayIndexExpression;
    }
    
    public String toString() {
        return String.format("variableReferenceNode(%s)", name);
    }

    public String getValue() {
        System.out.println("Value requested of valueless node, name returned instead.");
        return getName();
    }
    
    public String getName() {
        return name;
    }
}
