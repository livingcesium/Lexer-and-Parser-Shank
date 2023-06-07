public class BooleanCompareNode extends Node<Token>{
    
    Node leftTerm;
    Node rightTerm;
    private Token op; // FIXME: This is a placeholder
    
    BooleanCompareNode(Node left, Node right, Token op) {
        leftTerm = left;
        rightTerm = right;
        this.op = op;
    }
    
    public String toString(){
        if(rightTerm == null){
             return String.format("Solo Boolean Node (%s)", leftTerm.toString());
        }
        
        switch(op.getType()){
            case EQUAL:
                return String.format("Equals Node (%s = %s)", leftTerm.toString(), rightTerm.toString());
            case NOTEQUAL:
                return String.format("Not Equals Node (%s != %s)", leftTerm.toString(), rightTerm.toString());
            case LESS:
                return String.format("Less Than Node (%s < %s)", leftTerm.toString(), rightTerm.toString());
            case GREATER:
                return String.format("Greater Than Node (%s > %s)", leftTerm.toString(), rightTerm.toString());
            case LESSEQUAL:
                return String.format("Less Than or Equal Node (%s <= %s)", leftTerm.toString(), rightTerm.toString());
            case GREATEREQUAL:
                return String.format("Greater Than or Equal Node (%s >= %s)", leftTerm.toString(), rightTerm.toString());
            default:
                return String.format("Incomplete/broken BooleanCompareNode (%s ?? %s)", leftTerm.toString(), rightTerm.toString());
        }
    }
    
    public Token getValue() {
        return op;
    }
    
}
