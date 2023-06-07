public class MathOpNode extends Node<Token>{
    
    Node<Number> leftTerm;
    Node<Number> rightTerm;
    private Token op; // FIXME: This is a placeholder
    
    MathOpNode(Node left, Node right, Token op) {
        leftTerm = left;
        rightTerm = right;
        this.op = op;
    }
    
    public String toString() {
        switch(op.getType()){
            case PLUS:
                return String.format("Addition Node (%s + %s)", leftTerm.toString(), rightTerm.toString());
            case MINUS:
                return String.format("Subtraction Node (%s - %s)", leftTerm.toString(), rightTerm.toString());
            case MULTIPLY:
                return String.format("Multiplication Node (%s * %s)", leftTerm.toString(), rightTerm.toString());
            case DIVIDE:
                return String.format("Division Node (%s / %s)", leftTerm.toString(), rightTerm.toString());
            case MODULO:
                return String.format("Modulo Node (%s %% %s)", leftTerm.toString(), rightTerm.toString());
            default:
                return String.format("Incomplete/broken MathOpNode (%s ?? %s)", leftTerm.toString(), rightTerm.toString());
                
        }
    }
    
    public Token getValue() {
        return op;
    }
    
}
