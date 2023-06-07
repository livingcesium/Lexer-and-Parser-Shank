public class VariableNode<E> extends Node<E>{
    
    private E value;
    private String name;
    private boolean changeable;
    private String type;
    
    int from;
    int to;
    
    VariableNode(E value, String name, String type, boolean changeable) {
        this.value = value;
        this.name = name;
        this.type = type;
        this.changeable = changeable;
    }

    VariableNode(String name, String type, boolean changeable) {
        this.name = name;
        this.type = type;
        this.changeable = changeable;
    }
    
    public String toString() {
        if(value == null)
            return String.format("variableNode(%s : %s)", name, type);
        else 
            return String.format("variableNode(%s : %s = %s)", name, type, value);
    }

    public E getValue() {
        return value;
    }
    public void setFrom(int from) {
        this.from = from;
    }
    public void setTo(int to) {
        this.to = to;
    }
}
