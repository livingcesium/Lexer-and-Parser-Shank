public class RealNode extends Node<Float>{
    
    private float value;
    
    RealNode(float value) {
        this.value = value;
    }
    
    
    public String ToString() {
        return String.format("realNode(%f)", value);
    }
    
    public Float getValue() {
        return value;
    }
}
