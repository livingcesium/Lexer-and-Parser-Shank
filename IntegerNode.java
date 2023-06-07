public class IntegerNode extends Node<Integer>{
    
    private int value;
    
    IntegerNode(int value) {
        this.value = value;
    }
    
    public String toString() {
        return String.format("intNode(%d)", value);
    }

    public Integer getValue() {
        return value;
    }
}
