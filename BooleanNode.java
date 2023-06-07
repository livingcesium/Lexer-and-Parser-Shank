public class BooleanNode extends Node<Boolean>{
    
    private Boolean value;
    
    BooleanNode(Boolean value) {
        this.value = value;
    }
    
    public String toString() {
        return String.format("boolNode(%s)", value);
    }

    public Boolean getValue() {
        return value;
    }
}
