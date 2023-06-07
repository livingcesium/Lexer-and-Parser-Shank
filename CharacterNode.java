public class CharacterNode extends Node<Character>{
    
    private Character value;
    
    CharacterNode(Character value) {
        this.value = value;
    }
    
    public String toString() {
        return String.format("charNode(%s)", value);
    }

    public Character getValue() {
        return value;
    }
}
