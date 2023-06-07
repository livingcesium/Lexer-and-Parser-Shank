public class AssignmentNode extends StatementNode{
    
    VariableReferenceNode variableReference;
    private Node value;
    
    AssignmentNode(VariableReferenceNode variableReference, Node value) {
        this.variableReference = variableReference;
        this.value = value;
    }
    
    public String toString() {
        return String.format("assignmentNode(%s := %s)", variableReference, value);
    }
    
    public Node getValue() {
        return value;
    }
    
}

