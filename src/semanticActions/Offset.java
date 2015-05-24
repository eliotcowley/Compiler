package semanticActions;

import lex.Token;
import lex.TokenType;

/**
 * Offset (subscript) for array entries. For instance, if x is an array and
 * we are trying to access x[1], 1 is the offset. If an entry is a simple
 * variable, offset is set to null.
 * @author elcowley
 */
public class Offset extends Token {
    
    private int value; // value of the offset
    private boolean isNull;
    
    /**
     * Constructor for the Offset class. In this case, value would be null.
     */
    public Offset() {
        isNull = true;
        setType(TokenType.INTEGER);
    }
    
    /**
     * Constructor for the Offset class.
     * @param value Integer representing the offset.
     */
    public Offset(int value) {
        this.value = value;
        isNull = false;
        setType(TokenType.INTEGER);
    }
    
    /**
     * Returns whether the offset is null or not.
     * @return Boolean - true if null, false if not.
     */
    public boolean isNull() {
        return isNull;
    }
    
    /**
     * Returns the value of the offset.
     * @return int - value of offset.
     */
    public int getVal() {
        return value;
    }
    
}
