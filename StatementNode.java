public abstract class StatementNode<E> extends Node<E>{
    
    private E value;
    
    
    StatementNode(){
        this.value = null;
    }
    StatementNode(E value) {
        this.value = value;
    }
    
    public String toString() {
        return String.format("statementNode(%s)", value);
    }

    public E getValue() {
        return value;
    }
}
