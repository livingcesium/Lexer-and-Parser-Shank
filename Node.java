public abstract class Node<E> {
    
    private E value;
    
    
    
    Node(){
        this.value = null;
    }
    Node(E value) {
        this.value = value;
    }
    
    public String toString() {
        return null;
    }
    
    public E getValue() {
        return value;
    }
    
}
