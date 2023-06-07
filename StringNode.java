public class StringNode extends Node<String>{
    
    private String value;
    
    StringNode(String value) {
        this.value = value;
    }
    
    public String toString() {
        return String.format("stringNode(%s)", value);
    }

    public String getValue() {
        return value;
    }
}
