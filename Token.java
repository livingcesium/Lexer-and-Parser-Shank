public class Token {
    //fields
    private tokenType type;
    private String value;
    private int lineNum;

    //constructor
    public Token(tokenType type, String value, int lineNum) {
        this.type = type;
        this.value = value;
        this.lineNum = lineNum;
    }

    //construct token and automatically determine type
//    public Token(String value) {
//        this.value = value;
//        if (value.matches("[0-9]+")) { //regex for numbers and decimals: "[0-9]+(\\.[0-9]+)?"
//            this.type = tokenType.NUMBER;
//        } else if (value.matches("[a-zA-Z][a-zA-Z0-9]*")){ //alphanumeric regex for strings starting with a lettter
//            this.type = tokenType.WORD;
//        } else if {
//            this.type = tokenType.PUNCTUATION;
//        }
//    }

    //methods
    public tokenType getType() {
        return type;
    }
    public String getValue() {
        return value;
    }

    public void setType(tokenType type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getLineNum() {
        return lineNum;
    }
    
    public String toString() {
        return String.format("%s (%s) at line %d", type, value, lineNum);
    }
}
